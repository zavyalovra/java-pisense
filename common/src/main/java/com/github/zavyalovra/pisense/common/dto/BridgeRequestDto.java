package com.github.zavyalovra.pisense.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Список шин обязателен")
    @Valid
    private List<BusRequestDto> buses;
}
