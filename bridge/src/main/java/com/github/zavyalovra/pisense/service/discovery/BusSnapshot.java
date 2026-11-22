package com.github.zavyalovra.pisense.service.discovery;

import lombok.Getter;

import java.util.List;

@Getter
public class BusSnapshot {

    private final String name;
    private final List<SensorSnapshot> sensors;

    public BusSnapshot(String name, List<SensorSnapshot> sensors) {
        this.name = name;
        this.sensors = List.copyOf(sensors);
    }
}
