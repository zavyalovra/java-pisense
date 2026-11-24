package com.github.zavyalovra.pisense.service.mappers;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.common.dto.BusRequestDto;
import com.github.zavyalovra.pisense.common.dto.SensorRequestDto;
import com.github.zavyalovra.pisense.service.discovery.BusSnapshot;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BridgeMapper {

    public static BridgeRequestDto toDto(List<BusSnapshot> buses) {
        List<BusRequestDto> busDtos = buses.stream()
                .filter(bus -> !bus.getSensors().isEmpty())
                .map(BridgeMapper::toDto)
                .toList();

        return new BridgeRequestDto(busDtos);
    }

    private static BusRequestDto toDto(BusSnapshot bus) {
        List<SensorRequestDto> sensors = bus.getSensors().stream()
                .map(sensor -> SensorMapper.toDto(sensor.getDetected(), sensor.getChannels()))
                .toList();

        return new BusRequestDto(bus.getName(), sensors);
    }
}
