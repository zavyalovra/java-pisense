package com.github.zavyalovra.pisense.dao.mappers;

import com.github.zavyalovra.pisense.dto.MeasureRequestDto;
import com.github.zavyalovra.pisense.dto.MeasureResponseDto;
import com.github.zavyalovra.pisense.model.Measure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeasurementsMapper {

    public static MeasureResponseDto toMeasureResponseDto(Measure measure) {
        MeasureResponseDto dto = new MeasureResponseDto();
        dto.setId(measure.getId());
        dto.setSensor(measure.getSensor());
        dto.setData(measure.getMeasuredValue());
        dto.setReceivedAt(measure.getReceivedAt());

        return dto;
    }

    public static Measure toMeasure(MeasureRequestDto dto) {
        Measure measure = new Measure();
        measure.setId(dto.getBatchId());

        return measure;
    }
}
