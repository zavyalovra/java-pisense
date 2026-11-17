package com.github.zavyalovra.pisense.sensor;

import com.github.zavyalovra.pisense.common.model.BusType;
import com.github.zavyalovra.pisense.common.model.SensorType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DetectedSensorTest {

    private static SensorAddress address(BusType busType, String value) {
        return new SensorAddress(busType, value);
    }

    @Test
    void equalByAddressOnly() {
        DetectedSensor first = new DetectedSensor(
                address(BusType.I2C, "0x76"), SensorType.PRESSURE, "BMP280");
        DetectedSensor second = new DetectedSensor(
                address(BusType.I2C, "0x76"), SensorType.PRESSURE, "тот же датчик, другое описание");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void distinctRemovesDuplicatesFromDifferentBuses() {
        List<DetectedSensor> discovered = List.of(
                new DetectedSensor(address(BusType.I2C, "0x76"), SensorType.PRESSURE, "первый скан"),
                new DetectedSensor(address(BusType.I2C, "0x76"), SensorType.PRESSURE, "повторный скан"),
                new DetectedSensor(address(BusType.ONE_WIRE, "28-0000069c1e83"), SensorType.TEMPERATURE, "термометр")
        );

        List<DetectedSensor> unique = discovered.stream().distinct().toList();

        assertEquals(2, unique.size());
    }

    @Test
    void differentBusMeansDifferentSensor() {
        DetectedSensor i2c = new DetectedSensor(
                address(BusType.I2C, "0x23"), SensorType.LIGHT, "BH1750");
        DetectedSensor spi = new DetectedSensor(
                address(BusType.SPI, "0x23"), SensorType.LIGHT, "другая шина");

        assertNotEquals(i2c, spi);
    }
}
