package com.github.zavyalovra.pisense.client;

import com.github.zavyalovra.pisense.common.dto.BridgeRequestDto;
import com.github.zavyalovra.pisense.service.port.DiscoverySender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class Client implements DiscoverySender {
    private final RestClient restClient;
    private final String bridgeName;

    public Client(RestClient.Builder builder,
                  @Value("${pisense-server.url}") String baseUrl,
                  @Value("${pisense-server.connect-timeout-ms}") int connectMs,
                  @Value("${pisense-server.read-timeout-ms}") int readMs,
                  @Value("${pisense.bridge-name}") String bridgeName) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectMs);
        factory.setReadTimeout(readMs);
        this.restClient = builder.baseUrl(baseUrl).requestFactory(factory).build();
        this.bridgeName = bridgeName;
    }

    @Override
    public void sendDiscovery(BridgeRequestDto request) {
        restClient.post()
                .uri("/api/v1/bridges/{bridgeName}/discovery", bridgeName)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
