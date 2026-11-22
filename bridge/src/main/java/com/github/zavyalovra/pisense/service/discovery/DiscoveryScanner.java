package com.github.zavyalovra.pisense.service.discovery;

import com.github.zavyalovra.pisense.model.DetectedSensor;
import com.github.zavyalovra.pisense.model.Sensor;
import com.github.zavyalovra.pisense.model.SensorBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscoveryScanner {
    private final List<SensorBus> buses;

    public List<BusSnapshot> scan() {
        return buses.stream()
                .map(this::scanBus)
                .toList();
    }

    private BusSnapshot scanBus(SensorBus bus) {
        try {
            List<SensorSnapshot> sensors = bus.discover().stream()
                    .map(detected -> describe(bus, detected))
                    .flatMap(Optional::stream)
                    .toList();

            return new BusSnapshot(bus.getName(), sensors);
        } catch (RuntimeException e) {
            log.warn("discovery: шина {} не просканирована: {}", bus.getName(), e.getMessage());
            return new BusSnapshot(bus.getName(), List.of());
        }
    }

    private Optional<SensorSnapshot> describe(SensorBus bus, DetectedSensor detected) {
        try {
            Optional<Sensor> opened = bus.open(detected);
            if (opened.isEmpty()) {
                log.warn("discovery: нет драйвера для {}", detected.getSensorCode());
                return Optional.empty();
            }

            try (Sensor sensor = opened.get()) {
                return Optional.of(new SensorSnapshot(detected, sensor.channels()));
            }
        } catch (RuntimeException e) {
            log.warn("discovery: ошибка {}: {}", detected.getSensorCode(), e.getMessage());
            return Optional.empty();
        }
    }
}
