package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.SensorType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"busName", "address"})
public class DetectedSensor {
    private final String busName;
    private final SensorType sensorType;
    private final String address;
    private final String model;
    private final String serialNumber;
}
