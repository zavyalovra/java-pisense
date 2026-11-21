package com.github.zavyalovra.pisense.dto;

import com.github.zavyalovra.pisense.common.model.SensorStatus;
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
    private String bridgeName;
    private SensorStatus status;
    private List<SensorShortResponseDto> sensors;
    private Instant createdAt;
    private Instant lastSeenAt;
}
