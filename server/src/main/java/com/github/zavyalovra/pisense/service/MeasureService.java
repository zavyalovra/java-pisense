package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.dto.MeasureResponseDto;

import java.util.List;

public interface MeasureService {
    List<MeasureResponseDto> getMeasureLatest();
}
