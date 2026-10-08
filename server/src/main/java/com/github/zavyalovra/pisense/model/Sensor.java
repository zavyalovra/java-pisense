package com.github.zavyalovra.pisense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensors", schema = "public")
@Getter
@Setter
@ToString
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_type", nullable = false)
    private SensorType sensorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "bus_type", nullable = false)
    private BusType busType;

    @Column(name = "bus_address", nullable = false)
    private String busAddress;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensor)) return false;
        return id != null && id.equals(((Sensor) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
