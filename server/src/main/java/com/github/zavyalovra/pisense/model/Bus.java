package com.github.zavyalovra.pisense.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "buses", schema = "public")
@Getter
@Setter
@ToString
public class Bus extends BaseEntity {

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Sensor> sensors = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bridge_id", nullable = false)
    @ToString.Exclude
    private Bridge bridge;

    @Column(nullable = false)
    private String name;

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        sensor.setBus(this);
    }
}
