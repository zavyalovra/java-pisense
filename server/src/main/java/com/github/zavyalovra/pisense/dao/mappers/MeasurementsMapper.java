package com.github.zavyalovra.pisense.dao.mappers;

import com.github.zavyalovra.pisense.common.dto.MeasurementDto;
import com.github.zavyalovra.pisense.common.dto.SensorDto;
import com.github.zavyalovra.pisense.model.Measure;
import com.github.zavyalovra.pisense.model.Sensor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Преобразование сущностей в DTO общего контракта.
 * В БД время хранится как {@link LocalDateTime} в UTC, в JSON уходит {@link Instant}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeasurementsMapper {

    public static MeasurementDto toMeasurementDto(Measure measure) {
        if (measure == null) {
            return null;
        }
        MeasurementDto dto = new MeasurementDto();
        dto.setId(measure.getId());
        dto.setSensor(toSensorDto(measure.getSensor()));
        dto.setValue(measure.getMeasuredValue());
        dto.setMeasuredAt(toInstant(measure.getMeasuredAt()));
        dto.setReceivedAt(toInstant(measure.getReceivedAt()));
        return dto;
    }

    public static SensorDto toSensorDto(Sensor sensor) {
        if (sensor == null) {
            return null;
        }
        SensorDto dto = new SensorDto();
        dto.setId(sensor.getId());
        dto.setDescription(sensor.getDescription());
        dto.setType(sensor.getSensorType());
        dto.setBusType(sensor.getBusType());
        dto.setBusAddress(sensor.getBusAddress());
        dto.setAvailable(sensor.isAvailable());
        dto.setCreatedAt(toInstant(sensor.getCreatedAt()));
        return dto;
    }

    private static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.toInstant(ZoneOffset.UTC);
    }
}
