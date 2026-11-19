package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.SensorStatus;

import java.time.LocalDateTime;

public class Sensor {
    private Long id;
    private String address;
    private String model;
    private String serialNumber;
    private Long MeasurementClassId;
    private Long locationId;
    private SensorStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastSeenAt;
}
