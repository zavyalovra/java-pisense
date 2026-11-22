package com.github.zavyalovra.pisense.client;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class Client {
    private final RestClient restClient;
    private final Long bridgeId;

    public Client(RestClient.Builder builder,
                  @Value("${pisense-server.url}") String baseUrl,
                  @Value("${pisense-server.connect-timeout-ms}") int connectMs,
                  @Value("${pisense-server.read-timeout-ms}") int readMs,
                  @Value("${pisense.bridge-name}") long bridgeId) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectMs);
        factory.setReadTimeout(readMs);
        this.restClient = builder.baseUrl(baseUrl).requestFactory(factory).build();
        this.bridgeId = bridgeId;
    }

    public void sendDiscovery(List<BridgeRequestDto> request) {
        restClient.post()
                .uri("/api/v1/bridges/{bridgeName}/discovery", bridgeId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
