package com.github.zavyalovra.pisense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusResponseDto {
    private Long id;
    private String name;

    private List<SensorResponseDto> sensors;
}
