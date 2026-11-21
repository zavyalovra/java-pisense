package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.DeviceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "bridges", schema = "public")
@Getter
@Setter
@ToString
public class Bridge extends BaseEntity {

    /** pisense.bridge-id из application.properties */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status = DeviceStatus.DISCOVERED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant lastSeenAt;
}
