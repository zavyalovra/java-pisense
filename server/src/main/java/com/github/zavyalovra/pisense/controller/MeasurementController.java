package com.github.zavyalovra.pisense.controller;

import com.github.zavyalovra.pisense.common.dto.MeasurementDto;
import com.github.zavyalovra.pisense.service.MeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/measurements")
public class MeasurementController {
    private final MeasureService measureService;

    @Autowired
    public MeasurementController(MeasureService measureService) {
        this.measureService = measureService;
    }

    @GetMapping("/latest")
    public List<MeasurementDto> findMeasureLatest() {
        return measureService.getMeasureLatest();
    }
}
