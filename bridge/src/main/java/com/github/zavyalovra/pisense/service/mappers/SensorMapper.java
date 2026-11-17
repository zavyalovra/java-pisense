package com.github.zavyalovra.pisense.service.mappers;

import com.github.zavyalovra.pisense.common.dto.SensorDto;
import com.github.zavyalovra.pisense.sensor.DetectedSensor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorMapper {

    public static SensorDto toSensorDto(DetectedSensor sensor) {
        SensorDto dto = new SensorDto();
        dto.setType(sensor.getType());
        dto.setBusType(sensor.getAddress().getBusType());
        dto.setBusAddress(sensor.getAddress().getAddress());
        dto.setDescription(sensor.getDescription());
        dto.setAvailable(true);
        // id остаётся null,
        // unit вычисляется в SensorDto.getUnit() на основе SensorType
        return dto;
    }
}
