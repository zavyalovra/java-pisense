package com.github.zavyalovra.pisense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Результат приёма батча сервером.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchIngestResult {

    private UUID batchId;

    private int measurementsCount;

    /**
     * {@code true}, если батч с таким {@code batchId} уже обрабатывался
     * (ответ 200 OK), {@code false} — сохранён впервые (201 Created).
     */
    private boolean duplicate;

    private Instant receivedAt;
}
