package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.MeasurementDto;

import java.util.List;

public interface MeasureService {
    List<MeasurementDto> getMeasureLatest();
}
