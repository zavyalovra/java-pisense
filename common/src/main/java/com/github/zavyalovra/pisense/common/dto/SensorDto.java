package com.github.zavyalovra.pisense.common.dto;

import com.github.zavyalovra.pisense.common.model.BusType;
import com.github.zavyalovra.pisense.common.model.SensorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Зарегистрированный датчик. Используется в ответах bridge и server.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDto {

    /**
     * Идентификатор в БД server. У bridge собственных id нет, поэтому там поле
     * может быть {@code null}.
     */
    private Long id;

    private String description;

    private SensorType type;

    private BusType busType;

    private String busAddress;

    private boolean available;

    private Instant createdAt;

    /**
     * Единица измерения, вычисляемая по типу датчика. В запросах не передаётся,
     * попадает в JSON только на чтение.
     */
    public String getUnit() {
        return type == null ? null : type.getUnit();
    }
}
