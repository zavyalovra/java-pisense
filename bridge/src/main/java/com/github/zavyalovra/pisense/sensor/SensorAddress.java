package com.github.zavyalovra.pisense.sensor;

import com.github.zavyalovra.pisense.common.model.BusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Идентичность датчика: шина + адрес на этой шине.
 * Ключ реестра в сервисе, поэтому класс неизменяемый.
 */
@Getter
@EqualsAndHashCode
@ToString
public final class SensorAddress {

    private final BusType busType;
    private final String address;

    public SensorAddress(@NonNull BusType busType, String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("не определен адрес сенсора");
        }
        this.busType = busType;
        this.address = address.trim();
    }
}
