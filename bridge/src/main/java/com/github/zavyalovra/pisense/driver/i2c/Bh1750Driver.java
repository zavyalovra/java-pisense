package com.github.zavyalovra.pisense.driver.i2c;

import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.sensor.SensorReader;
import com.pi4j.io.i2c.I2C;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class Bh1750Driver implements SensorReader {
    private final I2C device;

    @Override
    public SensorType getType() {
        return SensorType.LIGHT;
    }

    @Override
    public double read() {
        // TODO: включить измерение (0x10), прочитать 2 байта, вернуть люксы
        return 0.0;
    }
}
