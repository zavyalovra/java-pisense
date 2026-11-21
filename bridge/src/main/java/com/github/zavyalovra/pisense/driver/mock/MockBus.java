package com.github.zavyalovra.pisense.driver.mock;

import com.github.zavyalovra.pisense.common.model.BusType;
import com.github.zavyalovra.pisense.common.model.MetricType;
import com.github.zavyalovra.pisense.model.ChannelInfo;
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
        return "i2c1";
    }

    @Override
    public BusType getType() {
        return BusType.I2C;
    }

    @Override
    public List<DetectedSensor> discover() {
        return List.of(
                new DetectedSensor(getName(), "0x76", "BME280", "BME-0001"),
                new DetectedSensor(getName(), "0x23", "BH1750", "BH-0001"));
    }

    @Override
    public Optional<Sensor> open(DetectedSensor d) {
        String model = d.getModel() == null ? "" : d.getModel();
        return switch (model) {
            case "BME280" -> Optional.of(new MockSensor(List.of(
                    new MockSensor.Signal(new ChannelInfo("temperature", MetricType.TEMPERATURE, "Cel"), 22, 3),
                    new MockSensor.Signal(new ChannelInfo("pressure", MetricType.PRESSURE, "hPa"), 1013, 5),
                    new MockSensor.Signal(new ChannelInfo("humidity", MetricType.HUMIDITY, "%"), 45, 10))));
            case "BH1750" -> Optional.of(new MockSensor(List.of(
                    new MockSensor.Signal(new ChannelInfo("illuminance", MetricType.ILLUMINANCE, "lx"), 300, 150))));
            default -> Optional.empty();
        };
    }
}
