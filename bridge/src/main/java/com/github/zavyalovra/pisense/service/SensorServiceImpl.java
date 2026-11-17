package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.SensorDto;
import com.github.zavyalovra.pisense.sensor.DetectedSensor;
import com.github.zavyalovra.pisense.sensor.SensorBus;
import com.github.zavyalovra.pisense.service.mappers.SensorMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SensorServiceImpl implements SensorService {
    private final List<SensorBus> buses;

    @Override
    public List<SensorDto> getSensors() {
        return buses.stream()
                .flatMap(bus -> discoverSafely(bus).stream())
                .distinct()
                .map(SensorMapper::toSensorDto)
                .toList();
    }

    /**
     * Одна упавшая шина не должна ронять весь список:
     * 1-Wire может кинуть IOException, I2C — ошибку нативной библиотеки.
     */
    private List<DetectedSensor> discoverSafely(SensorBus bus) {
        try {
            return bus.discover();
        } catch (RuntimeException e) {
            log.warn("Не удалось опросить шину: ", e);
            return List.of();
        }
    }
}
