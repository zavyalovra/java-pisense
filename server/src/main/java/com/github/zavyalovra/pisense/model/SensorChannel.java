package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.MetricType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;

@Entity
@Table(name = "sensor_channels", schema = "public")
@Getter
@Setter
@ToString
public class SensorChannel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    @ToString.Exclude
    private Sensor sensor;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    private String unit;
}
