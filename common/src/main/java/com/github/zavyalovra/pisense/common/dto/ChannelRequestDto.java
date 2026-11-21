package com.github.zavyalovra.pisense.common.dto;

import com.github.zavyalovra.pisense.common.model.SensorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRequestDto {
    private String hardwareChannel;
    private SensorType sensorType;
}
