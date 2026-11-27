package com.github.zavyalovra.pisense.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorRequestDto {

    @NotBlank
    @Size(max = 100)
    private String sensorCode;

    @NotBlank
    @Size(max = 50)
    private String address;

    @Size(max = 100)
    private String model;

    @Size(max = 50)
    private String serialNumber;

    @Size(max = 100)
    private String location;

    @NotNull
    @Valid
    private List<ChannelRequestDto> channels;
}
