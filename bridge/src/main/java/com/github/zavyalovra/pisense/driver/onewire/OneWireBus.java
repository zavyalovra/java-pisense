package com.github.zavyalovra.pisense.driver.onewire;

import com.github.zavyalovra.pisense.common.model.BusType;
import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.sensor.DetectedSensor;
import com.github.zavyalovra.pisense.sensor.SensorAddress;
import com.github.zavyalovra.pisense.sensor.SensorBus;
import com.github.zavyalovra.pisense.sensor.SensorReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Profile("pi")
@Component
@Slf4j
public class OneWireBus implements SensorBus {
    private final Path devicesPath;

    public OneWireBus(@Value("${pisense.w1.path:/sys/bus/w1/devices}") String devicesPath) {
        this.devicesPath = Path.of(devicesPath);
    }

    @Override
    public List<DetectedSensor> discover() {
        try (Stream<Path> dirs = Files.list(devicesPath)) {
            return dirs.filter(Files::isDirectory)
                    .map(p -> p.getFileName().toString())
                    .filter(id -> id.matches("^[0-9a-f]{2}-[0-9a-f]{12}$"))
                    .map(this::toDetected)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (IOException e) {
            log.warn("1-Wire недоступен: {}", devicesPath, e);
            return List.of();
        }
    }

    @Override
    public Optional<SensorReader> open(DetectedSensor sensor) {
        if (sensor.getType() != SensorType.TEMPERATURE) {
            return Optional.empty();
        }
        Path w1Slave = devicesPath
                .resolve(sensor.getAddress().getAddress())
                .resolve("w1_slave");

        return Optional.of(new Ds18b20Driver(w1Slave));
    }

    private Optional<DetectedSensor> toDetected(String deviceId) {
        // deviceId выглядит как "28-0000069c1e83": family code + serial
        SensorType type = switch (deviceId.substring(0, 2)) {
            case "10", "22", "28", "3b" -> SensorType.TEMPERATURE;
            default -> null;
        };

        if (type == null) {
            log.debug("Неизвестное 1-Wire устройство: {}", deviceId);
            return Optional.empty();
        }

        return Optional.of(new DetectedSensor(
                new SensorAddress(BusType.ONE_WIRE, deviceId),
                type,
                "1-Wire " + deviceId
        ));
    }
}
