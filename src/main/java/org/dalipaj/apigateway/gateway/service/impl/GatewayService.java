package org.dalipaj.apigateway.gateway.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.gateway.service.IGatewayService;
import org.dalipaj.apigateway.gateway.service.IProxyService;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategyFactory;
import org.dalipaj.apigateway.rateLimit.RateLimitException;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
import org.dalipaj.apigateway.route.RouteUtil;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.response.RouteResponseDto;
import org.dalipaj.apigateway.route.service.IRouteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GatewayService implements IGatewayService {

    @Value("${app.response-ttl-minutes}")
    private Long responseTtlMinutes;
    private final IRouteService routeService;
    private final IRateLimitService rateLimitService;
    private final LoadBalancerStrategyFactory loadBalancerStrategyFactory;
    private final IProxyService proxyService;

    @Override
    public RouteResponseDto routeAndPrepareResponse(HttpServletRequest req,
                                                    Object requestBody) throws RateLimitException {
        rateLimitService.allowRequest(req);

        var pathWithQueryParams = RouteUtil.getPathWithQueryParams(req);

        var cachedResponse = routeService.getRouteResponseFromCache(pathWithQueryParams);
        if (cachedResponse != null && shouldGetResponseFromCache(cachedResponse, pathWithQueryParams))
            return cachedResponse.getResponse();

        var route = routeService.getRouteForRequest(req.getRequestURI());
        var loadBalancerStrategy = loadBalancerStrategyFactory.getStrategy(route.getLoadBalancerType());
        var chosenBackend = loadBalancerStrategy.chooseBackend(route.getBackends());

        var responseWithMetadata = proxyService.proxyRequest(req, requestBody, route, chosenBackend);
        routeService.saveRouteResponseInCache(responseWithMetadata);

        return responseWithMetadata.getResponse();
    }

    private boolean shouldGetResponseFromCache(RouteRedisResponseWithMetadata cachedResponse, String path) {
        var timeSinceLastCachedPlusTtl = cachedResponse.getLastCached().plusMinutes(responseTtlMinutes);
        var isExactPathWithCachedResponse = path.equalsIgnoreCase(cachedResponse.getExactPath());

        return timeSinceLastCachedPlusTtl.isAfter(LocalDateTime.now())
                && isExactPathWithCachedResponse
                && cachedResponse.getResponse() != null;
    }
}
