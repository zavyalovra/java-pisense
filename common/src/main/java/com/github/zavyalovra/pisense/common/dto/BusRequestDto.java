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
public class BusRequestDto {

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotNull
    @Valid
    private List<SensorRequestDto> sensors;
}
