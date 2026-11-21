package com.github.zavyalovra.pisense.dto.mappers;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.common.model.DeviceStatus;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;
import com.github.zavyalovra.pisense.model.Bridge;
import com.github.zavyalovra.pisense.model.Sensor;
import com.github.zavyalovra.pisense.model.SensorChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BridgeMapper {
    public static Bridge toBridge(BridgeRequestDto bridgeDto) {
        Bridge bridge = new Bridge();

        bridge.setStatus(DeviceStatus.DISCOVERED);
        bridge.setCreatedAt(Instant.now());
        bridge.setLastSeenAt(null);

        return bridge;
    }

    public static BridgeResponseDto toDto(Bridge bridge, List<Sensor> sensors, Map<Long, List<SensorChannel>> channelsBySensorId) {
        BridgeResponseDto dto = new BridgeResponseDto();

        dto.setId(bridge.getId());
        dto.setBridgeName(bridge.getBridgeName());
        dto.setStatus(bridge.getStatus());
        dto.setCreatedAt(bridge.getCreatedAt());
        dto.setLastSeenAt(bridge.getLastSeenAt());

        return dto;
    }
}
