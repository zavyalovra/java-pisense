package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dao.BridgeRepository;
import com.github.zavyalovra.pisense.dao.BusRepository;
import com.github.zavyalovra.pisense.dao.SensorChannelRepository;
import com.github.zavyalovra.pisense.dao.SensorRepository;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BridgeServiceImpl implements BridgeService {
    private final BridgeRepository bridgeRepository;
    private final BusRepository busRepository;
    private final SensorRepository sensorRepository;
    private final SensorChannelRepository sensorChannelRepository;

    @Override
    public List<BridgeResponseDto> getAllBridge() {
        return null;
    }

    @Override
    public Optional<BridgeResponseDto> getBridge(Long bridgeId) {
        return Optional.empty();
    }

    @Override
    @Transactional
    public BridgeResponseDto discover(String bridgeName, BridgeRequestDto bridgeDto) {
        return null;
    }

    @Override
    public void deleteBridge(Long bridgeId) {
    }
}
