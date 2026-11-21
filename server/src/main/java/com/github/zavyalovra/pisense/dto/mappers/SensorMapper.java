package com.github.zavyalovra.pisense.dto.mappers;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.common.dto.ChannelRequestDto;
import com.github.zavyalovra.pisense.common.model.SensorStatus;
import com.github.zavyalovra.pisense.dto.*;
import com.github.zavyalovra.pisense.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SensorMapper {

    public static Sensor toSensor(BridgeRequestDto dto, Bus bus) {
        Sensor sensor = new Sensor();

        sensor.setBus(bus);
        sensor.setAddress(dto.getAddress());
        sensor.setModel(dto.getModel());
        sensor.setSerialNumber(dto.getSerialNumber());
        sensor.setStatus(SensorStatus.DISCOVERED);
        sensor.setCreatedAt(Instant.now());

        return sensor;
    }

    public static SensorChannel toChannel(ChannelRequestDto dto, Sensor sensor, MeasurementClass measurementClass) {
        SensorChannel channel = new SensorChannel();

        channel.setSensor(sensor);
        channel.setHardwareChannel(dto.getHardwareChannel());
        channel.setSensorType(dto.getSensorType());
        channel.setMeasurementClass(measurementClass);

        return channel;
    }

    public static SensorResponseDto toDto(Sensor sensor, List<SensorChannel> channels) {
        SensorResponseDto dto = new SensorResponseDto();

        dto.setId(sensor.getId());
        dto.setAddress(sensor.getAddress());
        dto.setModel(sensor.getModel());
        dto.setSerialNumber(sensor.getSerialNumber());

        Bus bus = sensor.getBus();
        if (bus != null) {
            dto.setBusId(bus.getId());
            dto.setBusName(bus.getBusName());
        }

        dto.setLocation(toLocationDto(sensor.getLocation()));
        dto.setStatus(sensor.getStatus());
        dto.setCreatedAt(sensor.getCreatedAt());
        dto.setLastSeenAt(sensor.getLastSeenAt());
        dto.setChannels(toChannelDtos(channels));

        return dto;
    }

    public static SensorShortResponseDto toShortDto(Sensor sensor, List<SensorChannel> channels) {
        SensorShortResponseDto dto = new SensorShortResponseDto();

        dto.setId(sensor.getId());
        dto.setStatus(sensor.getStatus());
        dto.setSensorChannels(toChannelDtos(channels));

        return dto;
    }

    public static ChannelResponseDto toChannelDto(SensorChannel channel) {
        MeasurementClass measurementClass = channel.getMeasurementClass();

        return new ChannelResponseDto(
                channel.getId(),
                channel.getHardwareChannel(),
                channel.getSensorType(),
                measurementClass == null ? null : measurementClass.getId());
    }

    private static List<ChannelResponseDto> toChannelDtos(List<SensorChannel> channels) {
        if (channels == null) {
            return List.of();
        }
        return channels.stream().map(SensorMapper::toChannelDto).toList();
    }

    private static LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }
        return new LocationDto(location.getId(), location.getName(), location.getDescription());
    }
}
