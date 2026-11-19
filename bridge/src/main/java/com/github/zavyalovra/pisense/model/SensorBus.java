package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.BusType;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс шины: обнаружить устройства и открыть драйвер под найденное.
 * Реализации живут в пакете driver
 */
public interface SensorBus {

    BusType getType();

    List<DetectedSensor> discover();              // кто физически есть на шине

    Optional<Sensor> open(DetectedSensor sensor); // создать драйвер под найденное устройство
}
