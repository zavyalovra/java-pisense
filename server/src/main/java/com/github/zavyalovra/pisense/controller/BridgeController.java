package com.github.zavyalovra.pisense.controller;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeShortResponseDto;
import com.github.zavyalovra.pisense.dto.BridgeStatusRequestDto;
import com.github.zavyalovra.pisense.dto.PageResponseDto;
import com.github.zavyalovra.pisense.service.BridgeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bridges")
@AllArgsConstructor
@Validated
public class BridgeController {
    private static final String DEFAULT_PAGE_SIZE = "20";
    private final BridgeService bridgeService;

    @PostMapping("/{bridgeName}/discovery")
    public BridgeResponseDto toDiscoverBridge(@PathVariable String bridgeName,
                                              @Valid @RequestBody BridgeRequestDto bridgeRequestDto) {
        return bridgeService.discover(bridgeName, bridgeRequestDto);
    }

    @GetMapping
    public PageResponseDto<BridgeShortResponseDto> findBridges(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return bridgeService.getAllBridge(from, size);
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
