package com.github.zavyalovra.pisense.common.dto;

import com.github.zavyalovra.pisense.common.model.MetricType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRequestDto {
    @NotBlank
    @Size(max = 50)
    private String name;

    private MetricType metricType;

    @Size(max = 20)
    private String unit;
}
