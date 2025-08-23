package org.dalipaj.apigateway.gateway.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.gateway.service.IGatewayService;
import org.dalipaj.apigateway.route.response.RouteRedisResponseDto;
import org.dalipaj.apigateway.route.service.IRouteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GatewayService implements IGatewayService {

    public static final String API_KEY_HEADER = "x-api-key";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    @Value("${app.response-ttl-minutes}")
    private Long responseTtlMinutes;
    private final IRouteService routeService;

    @Override
    public RouteRedisResponseDto routeAndPrepareResponse(HttpServletRequest req) {
        var path = req.getRequestURI();
        var method = req.getMethod();
        var route = routeService.getRouteForRequest(path, method);

        var cachedResponse = routeService.getRouteResponseFromCache(path);

        if (cachedResponse != null) {
            var timeSinceLastCachedPlusTtl = cachedResponse.getLastCached().plusMinutes(responseTtlMinutes);
            var isExactPathWithCachedResponse = path.equalsIgnoreCase(cachedResponse.getExactPath());

            if (timeSinceLastCachedPlusTtl.isAfter(LocalDateTime.now()) && isExactPathWithCachedResponse)
                return cachedResponse.getResponse();
        }

        return RouteRedisResponseDto.builder()
                .build();
    }
}
