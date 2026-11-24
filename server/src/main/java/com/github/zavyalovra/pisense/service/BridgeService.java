package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;

import java.util.List;
import java.util.Optional;

public interface BridgeService {

    List<BridgeResponseDto> getAllBridge();

    Optional<BridgeResponseDto> getBridge(Long bridgeId);

    BridgeResponseDto discover(String bridgeId, BridgeRequestDto bridgeDto);

    void deleteBridge(Long bridgeId);
}
