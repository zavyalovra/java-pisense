package com.github.zavyalovra.pisense.model;

import com.github.zavyalovra.pisense.common.model.MetricType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class ChannelInfo {
    private final String name;
    private final MetricType metricType;
    private final String unit;
}
