package org.dalipaj.apigateway.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.gateway.proxy.IProxyService;
import org.dalipaj.apigateway.gateway.proxy.ProxyRequest;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategyFactory;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
import org.dalipaj.apigateway.rateLimit.service.RateLimitException;
import org.dalipaj.apigateway.route.RouteUtil;
import org.dalipaj.apigateway.route.data.response.RouteResponseDto;
import org.dalipaj.apigateway.route.data.response.RouteResponseKey;
import org.dalipaj.apigateway.upstream.service.IUpstreamService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayService implements IGatewayService {

    private final IUpstreamService upstreamService;
    private final IRateLimitService rateLimitService;
    private final LoadBalancerStrategyFactory loadBalancerStrategyFactory;
    private final IProxyService proxyService;

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String DELIMITER = ",";
    public static final String EMPTY_OBJECT = "{}";
    public static final String HEADERS_SERIALIZATION_ERROR = "Failed to serialize request headers";

    private static final Set<String> CACHE_HEADERS_ALLOW_LIST = Set.of(
            "accept",
            "accept-language",
            "authorization",
            "content-type",
            "x-api-key",
            "x-api-token"
    );

    private static final ObjectMapper SORTED_HEADERS_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    @Override
    public RouteResponseDto serveResponse(HttpServletRequest req,
                                          Object requestBody) throws RateLimitException,
                                                                     NoSuchAlgorithmException {

        var clientIp = getClientIp(req);
        rateLimitService.allowRequest(req, clientIp);

        var redisKey = RouteResponseKey.builder()
                .exactPath(RouteUtil.getPathWithQueryParams(req))
                .allowedSortedHeaders(allowListAndSortHeaders(req))
                .httpMethod(req.getMethod())
                .build();

        var cachedResponse = upstreamService.getRouteResponseFromCache(redisKey);
        if (cachedResponse != null) {
            log.info("Cache hit for key: {}", redisKey);
            return cachedResponse.getResponse();
        }

        var route = upstreamService.getRouteForRequest(req.getRequestURI());
        var loadBalancerStrategy = loadBalancerStrategyFactory.getStrategy(route.getLoadBalancerType());
        var chosenTarget = loadBalancerStrategy.chooseTarget(upstreamService.getTargets(route), clientIp);

        var responseWithMetadata = proxyService.proxyRequest(ProxyRequest.builder()
                .httpRequest(req)
                .requestBody(requestBody)
                .target(chosenTarget)
                .redisKey(redisKey)
                .build());

        upstreamService.saveRouteResponseInCache(responseWithMetadata);

        return responseWithMetadata.getResponse();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader(X_FORWARDED_FOR);
        if (xfHeader != null)
            return xfHeader.split(DELIMITER)[0];

        return request.getRemoteAddr();
    }

    private String allowListAndSortHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) return EMPTY_OBJECT;

        SortedMap<String, String> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (name == null) continue;

            String normalized = name.trim().toLowerCase(Locale.ROOT);
            if (!CACHE_HEADERS_ALLOW_LIST.contains(normalized)) continue;

            String value = Collections.list(request.getHeaders(name))
                    .stream()
                    .map(v -> v == null ? Strings.EMPTY : v.trim())
                    .collect(Collectors.joining(DELIMITER));

            sorted.put(name, value);
        }

        try {
            return SORTED_HEADERS_MAPPER.writeValueAsString(sorted);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(HEADERS_SERIALIZATION_ERROR, e);
        }
    }

    public static String allowListAndSortHeaders(HttpHeaders headers) {
        if (headers == null || headers.isEmpty()) return EMPTY_OBJECT;

        SortedMap<String, String> sorted = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        headers.forEach((name, values) -> {
            if (name == null) return;

            String normalized = name.trim().toLowerCase(Locale.ROOT);
            if (!CACHE_HEADERS_ALLOW_LIST.contains(normalized)) return;

            String value = (values == null ? Collections.<String>emptyList() : values)
                    .stream()
                    .map(v -> v == null ? Strings.EMPTY : v.trim())
                    .collect(Collectors.joining(DELIMITER));

            sorted.put(name, value);
        });

        try {
            return SORTED_HEADERS_MAPPER.writeValueAsString(sorted);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(HEADERS_SERIALIZATION_ERROR, e);
        }
    }
}
