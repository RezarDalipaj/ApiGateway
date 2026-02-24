package org.dalipaj.apigateway.gateway;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.gateway.proxy.IProxyService;
import org.dalipaj.apigateway.gateway.proxy.ProxyRequest;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategyFactory;
import org.dalipaj.apigateway.rateLimit.service.RateLimitException;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
import org.dalipaj.apigateway.route.RouteUtil;
import org.dalipaj.apigateway.route.data.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.data.response.RouteResponseDto;
import org.dalipaj.apigateway.upstream.service.IUpstreamService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GatewayService implements IGatewayService {

    @Value("${app.response-ttl-minutes}")
    private Long responseTtlMinutes;
    private final IUpstreamService upstreamService;
    private final IRateLimitService rateLimitService;
    private final LoadBalancerStrategyFactory loadBalancerStrategyFactory;
    private final IProxyService proxyService;
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    @Override
    public RouteResponseDto routeAndPrepareResponse(HttpServletRequest req,
                                                    Object requestBody) throws RateLimitException, NoSuchAlgorithmException {

        var clientIp = getClientIp(req);
        rateLimitService.allowRequest(req, clientIp);

        var pathWithQueryParams = RouteUtil.getPathWithQueryParams(req);
        var httpMethod = HttpMethod.valueOf(req.getMethod());

        var cachedResponse = upstreamService.getRouteResponseFromCache(pathWithQueryParams, httpMethod);
        if (cachedResponse != null && shouldGetResponseFromCache(cachedResponse, pathWithQueryParams))
            return cachedResponse.getResponse();

        var route = upstreamService.getRouteForRequest(req.getRequestURI());
        var loadBalancerStrategy = loadBalancerStrategyFactory.getStrategy(route.getLoadBalancerType());
        var chosenBackend = loadBalancerStrategy.chooseBackend(upstreamService.getBackends(route), clientIp);

        var responseWithMetadata = proxyService.proxyRequest(ProxyRequest.builder()
                .httpRequest(req)
                .requestBody(requestBody)
                .backend(chosenBackend)
                .oauth(route.getOauth())
                .build());

        upstreamService.saveRouteResponseInCache(responseWithMetadata, httpMethod);

        return responseWithMetadata.getResponse();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader(X_FORWARDED_FOR);
        if (xfHeader != null)
            return xfHeader.split(",")[0];

        return request.getRemoteAddr();
    }

    private boolean shouldGetResponseFromCache(RouteRedisResponseWithMetadata cachedResponse, String path) {
        var timeSinceLastCachedPlusTtl = cachedResponse.getLastCached().plusMinutes(responseTtlMinutes);
        var isExactPathWithCachedResponse = path.equalsIgnoreCase(cachedResponse.getExactPath());

        return timeSinceLastCachedPlusTtl.isAfter(LocalDateTime.now())
                && isExactPathWithCachedResponse
                && cachedResponse.getResponse() != null;
    }
}
