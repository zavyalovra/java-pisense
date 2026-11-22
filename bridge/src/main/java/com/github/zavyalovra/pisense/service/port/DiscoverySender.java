package com.github.zavyalovra.pisense.service.port;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;

public interface DiscoverySender {

    void sendDiscovery(BridgeRequestDto request);
}
