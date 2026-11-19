package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.SensorType;

/**
 * Порт одного датчика: как в README — только чтение и тип.
 */
public interface Sensor {

    SensorType getType();

    double read();
}
