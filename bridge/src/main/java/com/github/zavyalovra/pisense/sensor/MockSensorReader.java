package com.github.zavyalovra.pisense.sensor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Эмулятор датчика: плавная синусоида вокруг базового значения плюс небольшой шум.
 */
public class MockSensorReader implements SensorReader {
    private final SensorType type;
    private final double base;
    private final double amplitude;

    public MockSensorReader(SensorType type, double base, double amplitude) {
        this.type = type;
        this.base = base;
        this.amplitude = amplitude;
    }

    @Override
    public SensorType getType() {
        return type;
    }

    @Override
    public BusType getBusType() {
        return null;
    }

    @Override
    public String getAddress() {
        return "";
    }

    @Override
    public double read() {
        double wave = Math.sin(System.currentTimeMillis() / 60_000.0);
        double noise = ThreadLocalRandom.current().nextDouble(-0.1, 0.1) * amplitude;
        return base + amplitude * wave + noise;
    }
}