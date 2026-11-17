package com.github.zavyalovra.pisense.driver.i2c;

import com.github.zavyalovra.pisense.common.model.SensorType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class I2cDeviceSpecTest {

    @Test
    void emptySpecMeansNoDevices() {
        assertTrue(I2cDeviceSpec.parseAll("").isEmpty());
        assertTrue(I2cDeviceSpec.parseAll("   ").isEmpty());
        assertTrue(I2cDeviceSpec.parseAll(null).isEmpty());
    }

    @Test
    void parsesHexAddressAndType() {
        List<I2cDeviceSpec> specs = I2cDeviceSpec.parseAll("0x76:PRESSURE,0x23:LIGHT");

        assertEquals(2, specs.size());
        assertEquals(0x76, specs.get(0).address());
        assertEquals(SensorType.PRESSURE, specs.get(0).type());
        assertEquals("I2C PRESSURE 0x76", specs.get(0).description());
        assertEquals(0x23, specs.get(1).address());
        assertEquals(SensorType.LIGHT, specs.get(1).type());
    }

    @Test
    void parsesDecimalAddressAndDescription() {
        I2cDeviceSpec spec = I2cDeviceSpec.parse("118:humidity:BME280");

        assertEquals(0x76, spec.address());
        assertEquals(SensorType.HUMIDITY, spec.type());
        assertEquals("BME280", spec.description());
    }

    @Test
    void rejectsInvalidEntries() {
        assertThrows(IllegalArgumentException.class, () -> I2cDeviceSpec.parse("0x76"));
        assertThrows(IllegalArgumentException.class, () -> I2cDeviceSpec.parse("0x76:UNKNOWN"));
        assertThrows(IllegalArgumentException.class, () -> I2cDeviceSpec.parse("0x80:PRESSURE"));
        assertThrows(IllegalArgumentException.class, () -> I2cDeviceSpec.parse("zz:PRESSURE"));
    }
}
