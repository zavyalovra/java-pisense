package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeShortResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeStatusRequestDto;

import java.util.List;

public interface BridgeService {

    List<BridgeShortResponseDto> getAllBridge();

    BridgeResponseDto getBridgeByName(String bridgeName);

    BridgeResponseDto discover(String bridgeId, BridgeRequestDto bridgeDto);

    BridgeShortResponseDto changeBridgeStatus(String bridgeName, BridgeStatusRequestDto bridgeRequestDto);

}
