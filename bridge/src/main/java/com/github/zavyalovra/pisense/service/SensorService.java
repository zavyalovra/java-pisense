package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.SensorDto;

import java.util.List;

public interface SensorService {

    List<SensorDto> getSensors();
}
