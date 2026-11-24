package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.service.discovery.DiscoveryScanner;
import com.github.zavyalovra.pisense.service.mappers.BridgeMapper;
import com.github.zavyalovra.pisense.service.port.DiscoverySender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscoveryService {
    private final DiscoveryScanner scanner;
    private final DiscoverySender sender;

    public void reportDiscovery() {
        BridgeRequestDto body = BridgeMapper.toDto(scanner.scan());

        if (body.getBuses().isEmpty()) {
            log.debug("discovery: отправлять нечего");
            return;
        }

        sender.sendDiscovery(body);

        int sensors = body.getBuses().stream()
                .mapToInt(bus -> bus.getSensors().size())
                .sum();

        log.info("discovery отправлен: шин {}, датчиков {}", body.getBuses().size(), sensors);
    }
}
