package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dao.BridgeRepository;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;

import com.github.zavyalovra.pisense.dto.BridgeShortResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeStatusRequestDto;
import com.github.zavyalovra.pisense.dto.PageResponseDto;
import com.github.zavyalovra.pisense.dto.mappers.BridgeMapper;
import com.github.zavyalovra.pisense.exception.NotFoundException;
import com.github.zavyalovra.pisense.exception.ValidationException;
import com.github.zavyalovra.pisense.model.Bridge;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BridgeServiceImpl implements BridgeService {
    private final BridgeRepository bridgeRepository;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "id");
    private static final int MAX_PAGE_SIZE = 100;

    @Override
    public PageResponseDto<BridgeShortResponseDto> getAllBridge(int from, int size) {
        if (from < 0) {
            throw new ValidationException("Параметр 'from' должен быть больше или равен 0");
        }
        if (size <= 0) {
            throw new ValidationException("Параметр 'size' должен быть больше 0");
        }

        // size больше максимума усекается, а не отклоняется
        int safeSize = Math.min(size, MAX_PAGE_SIZE);

        // from — смещение в элементах, оно должно попадать на границу страницы,
        // иначе from / safeSize молча округлит его вниз и клиент получит не те данные.
        if (from % safeSize != 0) {
            throw new ValidationException("Параметр 'from' должен быть кратен размеру страницы");
        }

        Pageable pageable = PageRequest.of(from / safeSize, safeSize, DEFAULT_SORT);

        Page<BridgeShortResponseDto> page = bridgeRepository.findAll(pageable)
                .map(BridgeMapper::toShortResponseDto);

        return PageResponseDto.of(page);
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
