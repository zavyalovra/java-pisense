package com.github.zavyalovra.pisense.driver.i2c;

import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.sensor.SensorReader;
import com.pi4j.io.i2c.I2C;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class Bmp280Driver implements SensorReader {
    private final I2C device;

    @Override
    public SensorType getType() {
        return SensorType.PRESSURE;
    }

    @Override
    public double read() {
        // TODO: калибровочные регистры + компенсация, вернуть давление в kPa
        return 0.0;
    }
}
