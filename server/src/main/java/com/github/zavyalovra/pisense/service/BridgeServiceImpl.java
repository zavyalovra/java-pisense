package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.common.model.DeviceStatus;
import com.github.zavyalovra.pisense.dao.BridgeRepository;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;

import com.github.zavyalovra.pisense.dto.BridgeShortResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeStatusRequestDto;
import com.github.zavyalovra.pisense.dto.mappers.BridgeMapper;
import com.github.zavyalovra.pisense.exception.NotFoundException;
import com.github.zavyalovra.pisense.model.Bridge;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BridgeServiceImpl implements BridgeService {
    private final BridgeRepository bridgeRepository;

    @Override
    public List<BridgeShortResponseDto> getAllBridge() {
        return bridgeRepository.findAll().stream()
                .map(BridgeMapper::toShortResponseDto)
                .toList();
    }

    @Override
    public BridgeResponseDto getBridgeByName(String bridgeName) {
        Bridge bridge = bridgeRepository.findByNameWithTree(bridgeName)
                .orElseThrow(() -> new NotFoundException("Bridge с name = " + bridgeName + " не найден"));

        return BridgeMapper.toResponseDto(bridge);
    }

    @Override
    @Transactional
    public BridgeShortResponseDto changeBridgeStatus(String bridgeName, BridgeStatusRequestDto bridgeRequestDto) {
        Bridge bridge = bridgeRepository.findBridgeByName(bridgeName)
                .orElseThrow(() -> new NotFoundException("Bridge с name = " + bridgeName + " не найден"));

        bridge.setLastSeenAt(Instant.now());
        bridge.setStatus(bridgeRequestDto.getStatus());
        bridgeRepository.saveAndFlush(bridge);

        return BridgeMapper.toShortResponseDto(bridge);
    }

    @Override
    @Transactional
    public BridgeResponseDto discover(String bridgeName, BridgeRequestDto bridgeDto) {
        // 1. Один SELECT: мост со всеми шинами, датчиками и каналами
        Bridge bridge = bridgeRepository.findByNameWithTree(bridgeName)
                .orElseGet(() -> BridgeMapper.toEntity(bridgeName));

        bridge.setLastSeenAt(Instant.now());

        // 2. Обновляем дерево в памяти данными из запроса
        BridgeMapper.merge(bridge, bridgeDto);

        // 3. Сохраняем и сразу выполняем INSERT/UPDATE
        bridgeRepository.saveAndFlush(bridge);

        // 4. Собираем ответ обходом дерева
        return BridgeMapper.toResponseDto(bridge);
    }
}
