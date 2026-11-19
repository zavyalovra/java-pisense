package com.github.zavyalovra.pisense.service.mappers;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.model.DetectedSensor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorMapper {

    public static BridgeRequestDto toDto(DetectedSensor sensor) {
        return new BridgeRequestDto(          // @AllArgsConstructor
                sensor.getBusType(),
                sensor.getSensorType(),
                sensor.getAddress(),
                sensor.getModel(),
                sensor.getSerialNumber()
        );
    }
}
