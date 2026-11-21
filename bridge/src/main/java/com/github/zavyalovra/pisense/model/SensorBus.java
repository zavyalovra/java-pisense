package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.BusType;

import java.util.List;
import java.util.Optional;

public interface SensorBus {

    String getName();      // i2c1, i2c2, spi0, onewire (-> buses.name)

    BusType getType();     // -> buses.type

    /** Кто физически есть на шине. Не должен бросать исключений наружу. */
    List<DetectedSensor> discover();

    /** Драйвер под найденное устройство; empty, если модель не поддерживается. */
    Optional<Sensor> open(DetectedSensor detected);
}
