package org.dalipaj.apigateway.gateway;

import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.rateLimit.RateLimitDto;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.dto.RouteTrie;
import org.dalipaj.apigateway.upstream.backend.BackendDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Getter
@Setter
public class GatewayCache {

    private RouteTrie routeTrie;
    private Map<String, RateLimitDto> rateLimits = new HashMap<>();
    private final Map<String, List<BackendDto>> upstreams = new ConcurrentHashMap<>();

    public void addRouteUpstreams(RouteDto routeDto) {
        upstreams.put(routeDto.getPath(), new CopyOnWriteArrayList<>(routeDto.getBackends()));
    }

    public List<BackendDto> getUpstreams(RouteDto routeDto) {
        return upstreams.getOrDefault(routeDto.getPath(), new ArrayList<>());
    }

    public Map<String, List<BackendDto>> getAllUpstreams() {
        return upstreams;
    }
}