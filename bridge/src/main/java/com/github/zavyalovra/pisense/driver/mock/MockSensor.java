package com.github.zavyalovra.pisense.driver.mock;

import com.github.zavyalovra.pisense.common.model.SensorType;
import com.github.zavyalovra.pisense.model.Sensor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Эмулятор датчика: плавная синусоида вокруг базового значения плюс небольшой шум.
 */
@Getter
@AllArgsConstructor
class MockSensor implements Sensor {
    private final SensorType type;
    private final double base;
    private final double amplitude;

    @Override
    public SensorType getType() {
        return type;
    }

    @Override
    public double read() {
        double wave = Math.sin(System.currentTimeMillis() / 60_000.0);
        double noise = ThreadLocalRandom.current().nextDouble(-0.1, 0.1) * amplitude;
        return base + amplitude * wave + noise;
    }
}