package com.github.zavyalovra.pisense.driver.mock;

import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.model.DetectedSensor;
import com.github.zavyalovra.pisense.model.SensorBus;
import com.github.zavyalovra.pisense.model.Sensor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Profile("mock")
@Component
public class MockBus implements SensorBus {

    @Override
    public String getName() {
        return "ONEWIRE";
    }

    @Override
    public List<DetectedSensor> discover() {
        return List.of(
                new DetectedSensor(this.getName(), SensorType.TEMPERATURE, "0x76", "DS18B20", "80-1234566"),
                new DetectedSensor(this.getName(), SensorType.TEMPERATURE, "0x71", "DS18B20", "26-3546789")
        );
    }

    @Override
    public Optional<Sensor> open(DetectedSensor sensor) {
        return Optional.of(new MockSensor(sensor.getSensorType(), 22.0, 3.0));
    }
}
