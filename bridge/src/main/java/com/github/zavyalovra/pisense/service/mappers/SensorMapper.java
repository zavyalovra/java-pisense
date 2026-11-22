package com.github.zavyalovra.pisense.service.mappers;

import com.github.zavyalovra.pisense.common.dto.ChannelRequestDto;
import com.github.zavyalovra.pisense.common.dto.SensorRequestDto;
import com.github.zavyalovra.pisense.model.DetectedSensor;
import com.github.zavyalovra.pisense.model.ChannelInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorMapper {

    public static SensorRequestDto toDto(DetectedSensor s, List<ChannelInfo> channels) {
        return new SensorRequestDto(
                s.getSensorCode(),            // busName + "-" + address, например i2c1-0x76
                s.getAddress(),
                s.getModel(),
                s.getSerialNumber(),
                channels.stream().map(SensorMapper::toDto).toList());
    }

    public static ChannelRequestDto toDto(ChannelInfo c) {
        return new ChannelRequestDto(c.getName(), c.getMetricType(), c.getUnit());
    }
}