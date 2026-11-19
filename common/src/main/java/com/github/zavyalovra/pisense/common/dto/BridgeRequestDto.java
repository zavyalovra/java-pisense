package com.github.zavyalovra.pisense.common.dto;

import com.github.zavyalovra.pisense.common.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BridgeRequestDto {
    private BusType busType;
    private SensorType sensorType;
    private String address;
    private String model;
    private String serialNumber;
}
