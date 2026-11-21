package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.BatchStatus;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "batches", schema = "public")
@Getter
@Setter
@ToString
public class Batch extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bridge_id", nullable = false)
    @ToString.Exclude
    private Bridge bridge;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    @CreationTimestamp
    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;
}