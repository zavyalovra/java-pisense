package com.github.zavyalovra.pisense.service.mappers;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.model.DetectedSensor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorMapper {

    public static BridgeRequestDto toDto(DetectedSensor s) {
        BridgeRequestDto dto = new BridgeRequestDto();

        dto.setBusName(s.getBusName());
        dto.setAddress(s.getAddress());
        dto.setModel(s.getModel());
        dto.setSerialNumber(s.getSerialNumber());

        return dto;
    }
}
