package com.github.zavyalovra.pisense.sensor;

import com.github.zavyalovra.pisense.common.model.SensorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Результат сканирования шины: что за устройство найдено и где.
 * Создаётся адаптером шины, дальше только читается.
 */
@Getter
@EqualsAndHashCode(of = {"address"})
@ToString
public final class DetectedSensor {

    private final SensorAddress address;
    private final SensorType type;
    private final String description;

    public DetectedSensor(@NonNull SensorAddress address, @NonNull SensorType type, String description) {
        this.address = address;
        this.type = type;
        this.description = description;
    }
}
