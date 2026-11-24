package com.github.zavyalovra.pisense.dto;

import com.github.zavyalovra.pisense.common.model.DeviceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BridgeShortResponseDto {
    private Long id;
    private String name;
    private DeviceStatus status;
    private Instant createdAt;
    private Instant lastSeenAt;
}
