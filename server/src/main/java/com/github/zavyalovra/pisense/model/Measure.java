package com.github.zavyalovra.pisense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurements", schema = "public")
@Getter
@Setter
@ToString
public class Measure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor")
    @ToString.Exclude
    private Sensor sensor;

    @Column(name = "measured_value", nullable = false)
    private Double measuredValue;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Measure)) return false;
        return id != null && id.equals(((Measure) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
