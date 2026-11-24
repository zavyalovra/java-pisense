package com.github.zavyalovra.pisense.dto;

import com.github.zavyalovra.pisense.common.dto.BusRequestDto;
import com.github.zavyalovra.pisense.common.dto.BusResponseDto;
import com.github.zavyalovra.pisense.common.model.DeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BridgeResponseDto {
    private Long id;
    private String name;
    private DeviceStatus status;
    private Instant createdAt;
    private Instant lastSeenAt;
    private List<BusResponseDto> buses;
}
