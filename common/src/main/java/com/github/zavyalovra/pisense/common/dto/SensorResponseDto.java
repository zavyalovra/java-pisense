package com.github.zavyalovra.pisense.common.dto;

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

    private Long busId;

    private String sensorCode;

    private String address;

    private String model;

    private String serialNumber;

    private String location;

    private Instant createdAt;

    private List<ChannelResponseDto> channels;
}
