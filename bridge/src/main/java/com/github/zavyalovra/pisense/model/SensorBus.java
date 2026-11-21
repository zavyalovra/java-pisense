package com.github.zavyalovra.pisense.model;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс шины: обнаружить устройства и открыть драйвер под найденное.
 * Реализации живут в пакете driver
 */
public interface SensorBus {

    String getName();

    List<DetectedSensor> discover();              // кто физически есть на шине

    Optional<Sensor> open(DetectedSensor sensor); // создать драйвер под найденное устройство
}
