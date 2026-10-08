package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.dao.MeasurementsRepository;
import com.github.zavyalovra.pisense.dao.mappers.MeasurementsMapper;
import com.github.zavyalovra.pisense.dto.MeasureResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MeasureServiceImpl implements MeasureService {
    private final MeasurementsRepository measurementsRepository;

    public MeasureServiceImpl(MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
    }

    @Override
    public List<MeasureResponseDto> getMeasureLatest() {
        return measurementsRepository.findMeasurementsLast().stream()
                .map(MeasurementsMapper::toMeasureResponseDto)
                .toList();
    }
}
