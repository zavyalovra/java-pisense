package com.github.zavyalovra.pisense.controller;

import com.github.zavyalovra.pisense.dto.MeasureResponseDto;
import com.github.zavyalovra.pisense.service.MeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/measurements")
public class MeasurementsController {
    private final MeasureService measureService;

    @Autowired
    public MeasurementsController(MeasureService measureService) {
        this.measureService = measureService;
    }

    @GetMapping("/latest")
    public List<MeasureResponseDto> findMeasureLatest() {
        return measureService.getMeasureLatest();
    }
}
