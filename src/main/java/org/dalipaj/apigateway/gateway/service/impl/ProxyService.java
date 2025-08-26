package org.dalipaj.apigateway.gateway.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.gateway.service.IProxyService;
import org.dalipaj.apigateway.route.RouteUtil;
import org.dalipaj.apigateway.route.backend.BackendDto;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.response.RouteResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    public RouteRedisResponseWithMetadata proxyRequest(HttpServletRequest request, Object requestBody,
                                                       RouteDto route, BackendDto backend) {
        var pathWithQueryParams = RouteUtil.getPathWithQueryParams(request);
        var url = backend.getUrl() + pathWithQueryParams;

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

        Mono<ClientResponse> responseMono;

        if (requestBody == null)
            responseMono = requestSpec.exchangeToMono(Mono::just);
        else
            responseMono = requestSpec.bodyValue(requestBody)
                    .exchangeToMono(Mono::just);

        // Forward response (headers and body)
        return responseMono
                .flatMap(clientResponse -> {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.putAll(clientResponse.headers().asHttpHeaders());

            return clientResponse.bodyToMono(Object.class)
                    .defaultIfEmpty("")
                    .map(body -> RouteRedisResponseWithMetadata.builder()
                            .exactPath(pathWithQueryParams)
                            .lastCached(LocalDateTime.now())
                            .response(RouteResponseDto.builder()
                                    .status((HttpStatus) clientResponse.statusCode())
                                    .headers(responseHeaders)
                                    .data(body)
                                    .build())
                            .build());
        }).block();
    }
}
