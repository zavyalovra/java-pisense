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
public class SensorResponseDto {
    private Long id;
    private String address;
    private String model;
    private String serialNumber;
    private Long busId;
    private String busName;
    private List<ChannelResponseDto> channels;
    private LocationDto location;
    private SensorStatus status;
    private Instant createdAt;
    private Instant lastSeenAt;
}
