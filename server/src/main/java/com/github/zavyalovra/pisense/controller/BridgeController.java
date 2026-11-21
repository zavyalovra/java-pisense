package com.github.zavyalovra.pisense.controller;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.dto.BridgeResponseDto;
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

    @GetMapping
    public List<BridgeResponseDto> getAllBridges() {
        return bridgeService.getAllBridge();
    }

    @PostMapping("/{bridgeId}/discovery")
    public BridgeResponseDto toDiscoverBridge(@PathVariable(name = "bridgeId") String bridgeName,
                                              @Valid @RequestBody List<BridgeRequestDto> bridgeRequestDtoList) {
        return bridgeService.addBridge(bridgeName, bridgeRequestDtoList);
    }
}
