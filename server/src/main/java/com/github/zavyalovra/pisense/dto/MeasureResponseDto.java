package com.github.zavyalovra.pisense.dto;

import com.github.zavyalovra.pisense.model.Sensor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasureResponseDto {

    private Long id;

    private Sensor sensor;

    private Double data;

    private LocalDateTime receivedAt;
}
