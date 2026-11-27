package com.github.zavyalovra.pisense.dto;

import com.github.zavyalovra.pisense.common.model.DeviceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BridgeStatusRequestDto {

    @NotNull(message = "Поле status обязательно")
    private DeviceStatus status;
}
