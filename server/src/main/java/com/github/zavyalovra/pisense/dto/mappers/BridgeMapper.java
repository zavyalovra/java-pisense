package com.github.zavyalovra.pisense.dto.mappers;


import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.common.dto.BusRequestDto;
import com.github.zavyalovra.pisense.common.dto.BusResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeShortResponseDto;
import com.github.zavyalovra.pisense.model.Bridge;
import com.github.zavyalovra.pisense.model.Bus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BridgeMapper {

    /** Создаёт новый мост (для первого discovery от неизвестного устройства). */
    public static Bridge toEntity(String bridgeName) {
        Bridge bridge = new Bridge();
        bridge.setName(bridgeName);

        return bridge;
    }

    /**
     * Накладывает данные из запроса на дерево моста:
     * существующие шины обновляются, новые добавляются.
     * Шины, которых нет в запросе, не удаляются (у датчиков могут быть измерения).
     */
    public static void merge(Bridge bridge, BridgeRequestDto dto) {
        if (dto.getBuses() == null) {
            return;
        }

        Map<String, Bus> known = new HashMap<>();
        for (Bus bus : bridge.getBuses()) {
            known.put(bus.getName(), bus);
        }

        for (BusRequestDto busDto : dto.getBuses()) {
            Bus bus = known.get(busDto.getName());
            if (bus == null) {
                bus = BusMapper.toEntity(busDto);
                bridge.addBus(bus);
                known.put(bus.getName(), bus);
            }
            BusMapper.merge(bus, busDto);
        }
    }

    public static BridgeResponseDto toResponseDto(Bridge bridge) {
        List<BusResponseDto> buses = bridge.getBuses().stream()
                .map(BusMapper::toResponseDto)
                .toList();

        return new BridgeResponseDto(
                bridge.getId(),
                bridge.getName(),
                bridge.getStatus(),
                bridge.getCreatedAt(),
                bridge.getLastSeenAt(),
                buses);
    }

    public static BridgeShortResponseDto toShortResponseDto(Bridge bridge) {
        return new BridgeShortResponseDto(
                bridge.getId(),
                bridge.getName(),
                bridge.getStatus(),
                bridge.getCreatedAt(),
                bridge.getLastSeenAt()
        );
    }
}
