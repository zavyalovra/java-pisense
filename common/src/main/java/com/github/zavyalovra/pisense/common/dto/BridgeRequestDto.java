package com.github.zavyalovra.pisense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BridgeRequestDto {
    private String busName;
    private String address;
    private String model;
    private String serialNumber;
    private List<ChannelRequestDto> channels;
}
