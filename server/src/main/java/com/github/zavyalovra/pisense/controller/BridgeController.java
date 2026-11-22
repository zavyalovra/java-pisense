package com.github.zavyalovra.pisense.controller;


import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;
import com.github.zavyalovra.pisense.service.BridgeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bridges")
@AllArgsConstructor
public class BridgeController {
    private final BridgeService bridgeService;

    @PostMapping("/{bridgeName}/discovery")
    public BridgeResponseDto toDiscoverBridge(@PathVariable String bridgeName,
                                              @Valid @RequestBody BridgeRequestDto bridgeRequestDto) {
        return bridgeService.addBridge(bridgeName, bridgeRequestDto);
    }
}
