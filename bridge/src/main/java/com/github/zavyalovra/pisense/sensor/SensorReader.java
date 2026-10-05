package com.github.zavyalovra.pisense.sensor;

public interface SensorReader {
    SensorType getType();

    double read();
}
