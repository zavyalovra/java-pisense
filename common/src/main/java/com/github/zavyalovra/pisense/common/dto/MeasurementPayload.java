package com.github.zavyalovra.pisense.common.dto;

import com.github.zavyalovra.pisense.common.model.BusType;
import com.github.zavyalovra.pisense.common.model.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Одно измерение внутри батча bridge → server.
 * Единица измерения не передаётся: её определяет server по полю {@code type}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementPayload {

    @NotNull
    private SensorType type;

    @NotNull
    private BusType busType;

    @NotBlank
    @Size(max = 64)
    private String busAddress;

    @NotNull
    private Double value;

    @NotNull
    private Instant measuredAt;
}
