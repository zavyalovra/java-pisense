package com.github.zavyalovra.pisense.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Тип датчика. Единица измерения в JSON не передаётся — она выводится из типа,
 * поэтому правило живёт здесь, в общем модуле, а не дублируется в сервисах.
 */
@Getter
@AllArgsConstructor
public enum SensorType {
    TEMPERATURE("C"),
    PRESSURE("kPa"),
    HUMIDITY("%"),
    LIGHT("lx");

    private final String unit;
}
