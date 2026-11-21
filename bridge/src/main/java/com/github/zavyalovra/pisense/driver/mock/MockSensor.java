package com.github.zavyalovra.pisense.driver.mock;

import com.github.zavyalovra.pisense.model.ChannelInfo;
import com.github.zavyalovra.pisense.model.Reading;
import com.github.zavyalovra.pisense.model.Sensor;
import lombok.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Эмулятор датчика: плавная синусоида вокруг базового значения плюс небольшой шум.
 */
final class MockSensor implements Sensor {

    /** Параметры эмулируемого сигнала одного канала. */
    @Value
    static class Signal {
        ChannelInfo channel;
        double base;
        double amplitude;
    }

    private final List<Signal> signals;

    MockSensor(List<Signal> signals) {
        this.signals = signals;
    }

    @Override
    public List<ChannelInfo> channels() {
        return signals.stream()
                .map(Signal::getChannel)
                .toList();
    }

    @Override
    public List<Reading> read() {
        return signals.stream()
                .map(s -> new Reading(s.getChannel().getName(), value(s)))
                .toList();
    }

    private static double value(Signal s) {
        double wave = Math.sin(System.currentTimeMillis() / 60_000.0);
        double noise = ThreadLocalRandom.current().nextDouble(-0.1, 0.1) * s.getAmplitude();
        return s.getBase() + s.getAmplitude() * wave + noise;
    }

}