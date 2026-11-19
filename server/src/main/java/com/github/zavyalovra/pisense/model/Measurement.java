package com.github.zavyalovra.pisense.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Measurement {
    private Long id;
    private UUID uuid;
    private Long batchId;
    private Long sensorId;
    private Double measurementValue;
    private LocalDateTime measurementAt;
    private LocalDateTime receivedAt;
}