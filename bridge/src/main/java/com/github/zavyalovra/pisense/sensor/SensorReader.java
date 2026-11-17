package com.github.zavyalovra.pisense.sensor;

import com.github.zavyalovra.pisense.common.model.SensorType;

/**
 * Порт одного датчика: как в README — только чтение и тип.
 */
public interface SensorReader {

    SensorType getType();

    double read();
}
