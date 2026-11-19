package com.github.zavyalovra.pisense.service;

import com.github.zavyalovra.pisense.client.Client;
import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.model.SensorBus;
import com.github.zavyalovra.pisense.service.mappers.SensorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DiscoveryService {
    private final List<SensorBus> buses;
    private final Client client;

    @Autowired
    public DiscoveryService(List<SensorBus> buses, Client client) {
        this.buses = buses;
        this.client = client;
    }

    public void reportDiscovery() {
        List<BridgeRequestDto> body = buses.stream()
                .flatMap(bus -> bus.discover().stream())
                .map(SensorMapper::toDto)
                .toList();

        log.debug("discovery: найдено датчиков {}", body.size());
        if (body.isEmpty()) {                 // нечего отправлять
            log.debug("discovery: отправлять нечего");
            return;
        }
        client.sendDiscovery(body);
        log.info("discovery отправлен: {}", body);
    }
}
