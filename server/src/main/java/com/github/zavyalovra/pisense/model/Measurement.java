package com.github.zavyalovra.pisense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "measurements", schema = "public")
@Getter
@Setter
@ToString
public class Measurement extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    @ToString.Exclude
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    @ToString.Exclude
    private SensorChannel channel;

    @Column(name = "measurement_value", nullable = false)
    private Double measurementValue;

    @Column(name = "measured_at", nullable = false)
    private Instant measuredAt;
}