package com.github.zavyalovra.pisense.common.dto;

import com.github.zavyalovra.pisense.common.model.MetricType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelResponseDto {
    private Long id;

    private Long sensorId;

    private String name;

    private MetricType metricType;

    private String unit;
}
