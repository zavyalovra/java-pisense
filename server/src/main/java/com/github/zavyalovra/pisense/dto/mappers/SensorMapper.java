package com.github.zavyalovra.pisense.dto.mappers;

import com.github.zavyalovra.pisense.common.dto.ChannelRequestDto;
import com.github.zavyalovra.pisense.common.dto.ChannelResponseDto;
import com.github.zavyalovra.pisense.common.dto.SensorRequestDto;
import com.github.zavyalovra.pisense.common.dto.SensorResponseDto;
import com.github.zavyalovra.pisense.model.Sensor;
import com.github.zavyalovra.pisense.model.SensorChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorMapper {

    public static Sensor toEntity(SensorRequestDto dto) {
        Sensor sensor = new Sensor();
        sensor.setSensorCode(dto.getSensorCode());
        return sensor;
    }

    public static void merge(Sensor sensor, SensorRequestDto dto) {
        sensor.setAddress(dto.getAddress());
        sensor.setModel(dto.getModel());
        sensor.setSerialNumber(dto.getSerialNumber());

        if (dto.getChannels() == null) {
            return;
        }

        Map<String, SensorChannel> known = new HashMap<>();
        for (SensorChannel channel : sensor.getSensorChannels()) {
            known.put(channel.getName(), channel);
        }

        for (ChannelRequestDto channelDto : dto.getChannels()) {
            SensorChannel channel = known.get(channelDto.getName());
            if (channel == null) {
                channel = toEntity(channelDto);
                sensor.addChannel(channel);
                known.put(channel.getName(), channel);
            }
            merge(channel, channelDto);
        }
    }

    public static SensorResponseDto toResponseDto(Sensor sensor) {
        List<ChannelResponseDto> channels = sensor.getSensorChannels().stream()
                .map(SensorMapper::toResponseDto)
                .toList();

        return new SensorResponseDto(
                sensor.getId(),
                sensor.getBus().getId(),
                sensor.getSensorCode(),
                sensor.getAddress(),
                sensor.getModel(),
                sensor.getSerialNumber(),
                sensor.getLocation(),
                sensor.getCreatedAt(),
                channels);
    }

    // ---------- SensorChannel ----------
    public static SensorChannel toEntity(ChannelRequestDto dto) {
        SensorChannel channel = new SensorChannel();
        channel.setName(dto.getName());
        return channel;
    }

    public static void merge(SensorChannel channel, ChannelRequestDto dto) {
        channel.setMetricType(dto.getMetricType());
        channel.setUnit(dto.getUnit());
    }

    public static ChannelResponseDto toResponseDto(SensorChannel channel) {
        return new ChannelResponseDto(
                channel.getId(),
                channel.getSensor().getId(),
                channel.getName(),
                channel.getMetricType(),
                channel.getUnit());
    }
}
