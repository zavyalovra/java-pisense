package com.github.zavyalovra.pisense.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SensorType {
    TEMPERATURE("C"),
    PRESSURE("kPa"),
    HUMIDITY("%"),
    LIGHT("lx");

    private final String unit;
}
