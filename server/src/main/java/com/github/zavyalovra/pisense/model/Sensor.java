package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.SensorStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "sensors", schema = "public")
@Getter
@Setter
@ToString
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Шина, к которой подключён датчик: sensor → bus → bridge. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false)
    @ToString.Exclude
    private Bus bus;

    /** Адрес на шине; уникален в пределах шины. */
    @Column(nullable = false)
    private String address;

    private String model;

    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @ToString.Exclude
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorStatus status;

    private Instant createdAt;

    private Instant lastSeenAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensor)) return false;
        return id != null && id.equals(((Sensor) o).getId());
    }

    @Override
    public int hashCode() {
        return Sensor.class.hashCode();
    }
}
