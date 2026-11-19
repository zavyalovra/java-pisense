package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.BatchStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Batch {
    private Long id;
    private UUID uuid;
    private BatchStatus status;
    private LocalDateTime measuredAt;
    private LocalDateTime receivedAt;
}