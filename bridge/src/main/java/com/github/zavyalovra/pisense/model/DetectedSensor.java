package com.github.zavyalovra.pisense.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"busName", "address"})
public class DetectedSensor {
    private final String busName;
    private final String address;
    private final String model;
    private final String serialNumber;

    /** Ключ датчика для сервера: sensors.sensor_code, например "i2c1-0x76". */
    public String getSensorCode() {
        return busName + "-" + address;
    }
}
