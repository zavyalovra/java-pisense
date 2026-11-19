package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;

import java.util.List;

public interface BridgeService {

    BridgeResponseDto createBridge(Long bridgeId, List<BridgeRequestDto> sensors);
}
