package com.github.zavyalovra.pisense.dto.mappers;

import com.github.zavyalovra.pisense.common.dto.BusRequestDto;
import com.github.zavyalovra.pisense.common.dto.BusResponseDto;
import com.github.zavyalovra.pisense.common.dto.SensorRequestDto;
import com.github.zavyalovra.pisense.common.dto.SensorResponseDto;
import com.github.zavyalovra.pisense.model.Bus;
import com.github.zavyalovra.pisense.model.Sensor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusMapper {

    public static Bus toEntity(BusRequestDto dto) {
        Bus bus = new Bus();
        bus.setName(dto.getName());
        return bus;
    }

    public static void merge(Bus bus, BusRequestDto dto) {
        if (dto.getSensors() == null) {
            return;
        }

        Map<String, Sensor> known = new HashMap<>();
        for (Sensor sensor : bus.getSensors()) {
            known.put(sensor.getSensorCode(), sensor);
        }

        for (SensorRequestDto sensorDto : dto.getSensors()) {
            Sensor sensor = known.get(sensorDto.getSensorCode());
            if (sensor == null) {
                sensor = SensorMapper.toEntity(sensorDto);
                bus.addSensor(sensor);
                known.put(sensor.getSensorCode(), sensor);
            }
            SensorMapper.merge(sensor, sensorDto);
        }
    }

    public static BusResponseDto toResponseDto(Bus bus) {
        List<SensorResponseDto> sensors = bus.getSensors().stream()
                .map(SensorMapper::toResponseDto)
                .toList();

        return new BusResponseDto(bus.getId(), bus.getName(), sensors);
    }
}