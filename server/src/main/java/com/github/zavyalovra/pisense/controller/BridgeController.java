package com.github.zavyalovra.pisense.controller;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeShortResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeStatusRequestDto;
import com.github.zavyalovra.pisense.service.BridgeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bridges")
@AllArgsConstructor
public class BridgeController {
    private final BridgeService bridgeService;

    @PostMapping("/{bridgeName}/discovery")
    public BridgeResponseDto toDiscoverBridge(@PathVariable String bridgeName,
                                              @Valid @RequestBody BridgeRequestDto bridgeRequestDto) {
        return bridgeService.discover(bridgeName, bridgeRequestDto);
    }

    @GetMapping
    public List<BridgeShortResponseDto> findBridges() {
        return bridgeService.getAllBridge();
    }

    @GetMapping("/{bridgeName}")
    public BridgeResponseDto findBridgesByName(@PathVariable String bridgeName) {
        return bridgeService.getBridgeByName(bridgeName);
    }

    @PatchMapping("/{bridgeName}")
    public BridgeShortResponseDto changeBridgeStatus(@PathVariable String bridgeName,
                                                     @Valid @RequestBody BridgeStatusRequestDto bridgeRequestDto) {
        return bridgeService.changeBridgeStatus(bridgeName, bridgeRequestDto);
    }
}
