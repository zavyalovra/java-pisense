package com.github.zavyalovra.pisense.scheduler;

import com.github.zavyalovra.pisense.service.DiscoveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscoveryScheduler {
    private final DiscoveryService discoveryService;

    @Autowired
    public DiscoveryScheduler(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Scheduled(fixedDelayString = "${pisense.poll-interval-ms}", initialDelayString = "5000")
    public void reportDiscovery() {
        try {
            discoveryService.reportDiscovery();
            log.debug("discovery отправлен");
        } catch (Exception e) {
            log.warn("discovery не отправлен: {}", e.getMessage());
        }
    }
}