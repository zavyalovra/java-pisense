package com.github.zavyalovra.pisense.sensor;

import java.util.List;
import java.util.Optional;

/**
 * Порт уровня шины: обнаружить устройства и открыть драйвер под найденное.
 * Реализации живут в пакете {@code driver} и не видны сервису.
 */
public interface SensorBus {

    List<DetectedSensor> discover();                    // кто физически есть на шине

    Optional<SensorReader> open(DetectedSensor sensor); // создать драйвер под найденное устройство
}
