package com.github.zavyalovra.pisense.driver.mock;

import com.github.zavyalovra.pisense.common.model.BusType;
import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.sensor.DetectedSensor;
import com.github.zavyalovra.pisense.sensor.SensorAddress;
import com.github.zavyalovra.pisense.sensor.SensorBus;
import com.github.zavyalovra.pisense.sensor.SensorReader;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Profile("mock")
@Component
public class MockBus implements SensorBus {

    @Override
    public List<DetectedSensor> discover() {
        return List.of(
                new DetectedSensor(new SensorAddress(BusType.I2C, "0x76"), SensorType.PRESSURE, "BMP280 (mock)"),
                new DetectedSensor(new SensorAddress(BusType.ONE_WIRE, "28-0000069C1E83"), SensorType.TEMPERATURE, "DS18B20 (mock)")
        );
    }

    @Override
    public Optional<SensorReader> open(DetectedSensor sensor) {
        return Optional.of(new MockSensorReader(sensor.getType(), 22.0, 3.0));
    }
}
