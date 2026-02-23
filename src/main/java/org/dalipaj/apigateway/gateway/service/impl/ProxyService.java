package org.dalipaj.apigateway.gateway.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.gateway.service.IProxyService;
import org.dalipaj.apigateway.route.RouteUtil;
import org.dalipaj.apigateway.upstream.backend.BackendDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.response.RouteResponseDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Enumeration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProxyService implements IProxyService {

    private final WebClient webClient;
    private static final String HOST_HEADER = "host";

    @Override
    public RouteRedisResponseWithMetadata proxyRequest(HttpServletRequest request,
                                                       Object requestBody,
                                                       BackendDto backend) {
        var pathWithQueryParams = RouteUtil.getPathWithQueryParams(request);
        var url = backend.getHost() + RouteUtil.removeServiceName(pathWithQueryParams);

        backend.incrementConnections();
        long start = System.currentTimeMillis();

        // Build request
        var requestSpec = webClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(url)
                .headers(headers -> {
                    Enumeration<String> headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        String headerName = headerNames.nextElement();
                        if (!headerName.equalsIgnoreCase(HOST_HEADER)) {
                            headers.addAll(headerName, Collections.list(request.getHeaders(headerName)));
                        }
                    }
                });

        WebClient.ResponseSpec responseSpec;

        if (requestBody == null) {
            responseSpec = requestSpec.retrieve();
        }
        else {
            responseSpec = requestSpec
                    .bodyValue(requestBody)
                    .retrieve();
        }

        ResponseEntity<Object> response = responseSpec
                .toEntity(Object.class)
                .block();

        // Forward response (headers and body)
        var responseWithMetadata = RouteRedisResponseWithMetadata.builder()
                .exactPath(pathWithQueryParams)
                .lastCached(LocalDateTime.now())
                .build();

        if (response != null) {
            responseWithMetadata.setResponse(RouteResponseDto.builder()
                            .status((HttpStatus) response.getStatusCode())
                            .headers(response.getHeaders())
                            .data(response.getBody())
                    .build());
        }

        backend.decrementConnections();
        backend.updateLatency(System.currentTimeMillis() - start);

        return responseWithMetadata;
    }
}
