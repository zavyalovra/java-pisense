package com.github.zavyalovra.pisense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "sensors", schema = "public")
@Getter
@Setter
@ToString
public class Sensor extends BaseEntity {

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SensorChannel> sensorChannels = new LinkedHashSet<>();

    /** Шина, к которой подключён датчик: sensor → bus → bridge. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false)
    @ToString.Exclude
    private Bus bus;

    @Column(name = "sensor_code", nullable = false)
    private String sensorCode;

    private String address;

    private String model;

    private String serialNumber;

    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public void addChannel(SensorChannel channel) {
        sensorChannels.add(channel);
        channel.setSensor(this);
    }
}
