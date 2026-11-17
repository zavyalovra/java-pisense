package com.github.zavyalovra.pisense.driver.i2c;

import com.github.zavyalovra.pisense.common.model.BusType;
import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.sensor.DetectedSensor;
import com.github.zavyalovra.pisense.sensor.SensorAddress;
import com.github.zavyalovra.pisense.sensor.SensorBus;
import com.github.zavyalovra.pisense.sensor.SensorReader;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Шина I2C. Список устройств задаётся вручную, потому что по адресу нельзя
 * однозначно определить тип чипа: 0x76 — это и BMP280, и BME280, и BMP180.
 * Присутствие проверяется чтением chip-id (для BMP/BME) или пробным чтением (BH1750).
 */
@Profile("pi")
@Component
@Slf4j
public class I2cBus implements SensorBus {

    private static final int CHIP_ID_REGISTER = 0xD0;
    private static final int BMP280_CHIP_ID = 0x58;
    private static final int BME280_CHIP_ID = 0x60;

    private final int bus;
    private final List<I2cDeviceSpec> configuredDevices;
    private final Map<Integer, I2C> openedDevices = new HashMap<>();

    private Context context;
    private boolean contextFailed;

    public I2cBus(@Value("${pisense.i2c.bus}") int bus,
                  @Value("${pisense.i2c.devices:}") String devicesSpec) {
        this.bus = bus;
        this.configuredDevices = I2cDeviceSpec.parseAll(devicesSpec);
    }

    @Override
    public List<DetectedSensor> discover() {
        List<DetectedSensor> detected = new ArrayList<>();
        for (I2cDeviceSpec spec : configuredDevices) {
            Optional<I2C> device = openDevice(spec.address());
            if (device.isEmpty() || !isPresent(device.get(), spec.type())) {
                continue;
            }
            detected.add(new DetectedSensor(
                    new SensorAddress(BusType.I2C, spec.addressAsHex()),
                    spec.type(),
                    spec.description()));
        }
        return detected;
    }

    @Override
    public Optional<SensorReader> open(DetectedSensor sensor) {
        int address;
        try {
            address = I2cDeviceSpec.parseAddress(sensor.getAddress().getAddress());
        } catch (IllegalArgumentException e) {
            log.warn("Некорректный I2C адрес: {}", sensor.getAddress().getAddress());
            return Optional.empty();
        }

        Optional<I2C> device = openDevice(address);
        if (device.isEmpty()) {
            return Optional.empty();
        }

        return switch (sensor.getType()) {
            case PRESSURE -> Optional.of(new Bmp280Driver(device.get()));
            case HUMIDITY -> Optional.of(new Bme280Driver(device.get()));
            case LIGHT -> Optional.of(new Bh1750Driver(device.get()));
            default -> Optional.empty();
        };
    }

    /**
     * Pi4J создаёт объект устройства без обращения к железу, поэтому кэшируем
     * его по адресу: сервис может вызывать open() на каждом цикле опроса.
     */
    private synchronized Optional<I2C> openDevice(int address) {
        I2C opened = openedDevices.get(address);
        if (opened != null) {
            return Optional.of(opened);
        }
        try {
            Context pi4j = context();
            if (pi4j == null) {
                return Optional.empty();
            }
            I2C device = pi4j.create(I2C.newConfigBuilder(pi4j)
                    .id("pisense-i2c-" + bus + "-" + String.format(Locale.ROOT, "%02x", address))
                    .bus(bus)
                    .device(address)
                    .build());
            openedDevices.put(address, device);
            return Optional.of(device);
        } catch (RuntimeException e) {
            log.debug("I2C устройство 0x{} недоступно на шине {}: {}",
                    Integer.toHexString(address), bus, e.getMessage());
            return Optional.empty();
        }
    }

    private synchronized Context context() {
        if (context == null && !contextFailed) {
            try {
                context = Pi4J.newAutoContext();
            } catch (RuntimeException e) {
                contextFailed = true;
                log.warn("Pi4J не смог инициализироваться, I2C-датчики недоступны: {}",
                        e.getMessage());
            }
        }
        return context;
    }

    private boolean isPresent(I2C device, SensorType type) {
        try {
            if (type == SensorType.LIGHT) {
                // У BH1750 нет регистра идентификации: достаточно, что устройство отвечает.
                device.read();
                return true;
            }
            int chipId = device.readRegister(CHIP_ID_REGISTER);
            boolean known = chipId == BMP280_CHIP_ID || chipId == BME280_CHIP_ID;
            if (!known) {
                log.debug("I2C 0x{}: неизвестный chip id 0x{}",
                        Integer.toHexString(device.device()), Integer.toHexString(chipId));
            }
            return known;
        } catch (RuntimeException e) {
            log.debug("I2C 0x{} не отвечает: {}",
                    Integer.toHexString(device.device()), e.getMessage());
            return false;
        }
    }

    @PreDestroy
    synchronized void shutdown() {
        openedDevices.clear();
        if (context != null) {
            try {
                context.shutdown();
            } catch (RuntimeException e) {
                log.warn("Не удалось остановить Pi4J: {}", e.getMessage());
            }
            context = null;
        }
    }
}
