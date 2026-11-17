package com.github.zavyalovra.pisense.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Батч измерений: тело {@code POST /api/v1/measurements} на server
 * и ответ {@code POST /api/v1/measurements/read} на bridge.
 * Идемпотентность обеспечивает {@code batchId}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDto {

    @NotNull
    private UUID batchId;

    @NotEmpty
    @Valid
    private List<MeasurementPayload> measurements;
}
