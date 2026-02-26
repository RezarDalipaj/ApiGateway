package org.dalipaj.apigateway.gateway.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.route.RouteUtil;
import org.dalipaj.apigateway.route.data.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.data.response.RouteResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.Enumeration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProxyService implements IProxyService {

    @Value("${app.upstreamTimeoutSeconds}")
    private Long upstreamTimeoutSeconds;

    private final WebClient webClient;
    private static final String HOST_HEADER = "Host";

    @Override
    public RouteRedisResponseWithMetadata proxyRequest(ProxyRequest proxyRequest) {
        var httpRequest = proxyRequest.getHttpRequest();
        var target = proxyRequest.getTarget();

        var pathToProxy = RouteUtil.removeServiceName(proxyRequest.getRedisKey().getExactPath());
        var url = target.getHost() + pathToProxy;

        target.incrementConnections();
        long start = System.currentTimeMillis();

        try {
            var requestSpec = webClient
                    .method(HttpMethod.valueOf(httpRequest.getMethod()))
                    .uri(url)
                    .headers(headers -> {
                        Enumeration<String> headerNames = httpRequest.getHeaderNames();
                        while (headerNames.hasMoreElements()) {
                            String headerName = headerNames.nextElement();
                            if (!headerName.equalsIgnoreCase(HOST_HEADER)) {
                                headers.addAll(headerName, Collections.list(httpRequest.getHeaders(headerName)));
                            }
                        }
                    });

            WebClient.ResponseSpec responseSpec;
            if (proxyRequest.getRequestBody() == null) {
                responseSpec = requestSpec.retrieve();
            } else {
                responseSpec = requestSpec.bodyValue(proxyRequest.getRequestBody()).retrieve();
            }

            ResponseEntity<Object> response = responseSpec
                    .toEntity(Object.class)
                    .timeout(Duration.ofSeconds(upstreamTimeoutSeconds))
                    .block();

            var responseWithMetadata = RouteRedisResponseWithMetadata.builder()
                    .key(proxyRequest.getRedisKey())
                    .build();

            if (response != null) {
                responseWithMetadata.setResponse(RouteResponseDto.builder()
                        .status((HttpStatus) response.getStatusCode())
                        .headers(response.getHeaders())
                        .data(response.getBody())
                        .build());
            }

            target.updateLatency(System.currentTimeMillis() - start);
            return responseWithMetadata;
        } finally {
            target.decrementConnections();
        }
    }
}
