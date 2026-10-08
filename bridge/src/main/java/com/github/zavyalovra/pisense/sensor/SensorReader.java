package com.github.zavyalovra.pisense.sensor;

public interface SensorReader {
    SensorType getType();

    BusType getBusType();

    String getAddress();

    double read();
}
