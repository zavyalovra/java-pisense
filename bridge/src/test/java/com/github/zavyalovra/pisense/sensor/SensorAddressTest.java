package com.github.zavyalovra.pisense.sensor;

import com.github.zavyalovra.pisense.common.model.BusType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SensorAddressTest {

    @Test
    void trimsAddress() {
        SensorAddress address = new SensorAddress(BusType.I2C, "  0x76  ");

        assertEquals("0x76", address.getAddress());
    }

    @Test
    void rejectsBlankAddress() {
        assertThrows(IllegalArgumentException.class,
                () -> new SensorAddress(BusType.I2C, "   "));
    }

    @Test
    void equalByBusAndAddress() {
        SensorAddress first = new SensorAddress(BusType.ONE_WIRE, "28-0000069c1e83");
        SensorAddress second = new SensorAddress(BusType.ONE_WIRE, " 28-0000069c1e83 ");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void differentBusMeansDifferentAddress() {
        SensorAddress i2c = new SensorAddress(BusType.I2C, "0x23");
        SensorAddress spi = new SensorAddress(BusType.SPI, "0x23");

        assertNotEquals(i2c, spi);
    }
}
