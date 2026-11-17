package com.github.zavyalovra.pisense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Сохранённое измерение — ответ bridge и server.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDto {

    private Long id;

    private SensorDto sensor;

    /**
     * Значение в единицах измерения {@link SensorDto#getUnit()}.
     */
    private Double value;

    private Instant measuredAt;

    private Instant receivedAt;
}
