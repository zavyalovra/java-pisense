package com.github.zavyalovra.pisense.controller;

import com.github.zavyalovra.pisense.common.dto.SensorDto;
import com.github.zavyalovra.pisense.service.SensorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Список датчиков, которые bridge видит на шинах.
 * TODO: добавить описание и id после появления реестра датчиков / синхронизации с server.
 */
@RestController
@RequestMapping("/api/v1/sensors")
public class SensorController {
    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public List<SensorDto> getSensors() {
        return sensorService.getSensors();
    }
}
