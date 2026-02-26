package org.dalipaj.apigateway.gateway.localcache;

import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.rateLimit.data.RateLimitDto;
import org.dalipaj.apigateway.route.data.RouteDto;
import org.dalipaj.apigateway.route.data.RouteTrie;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
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
    private final Map<String, List<TargetDto>> targets = new ConcurrentHashMap<>();

    public void addRouteTargets(RouteDto routeDto) {
        targets.put(routeDto.getPath(), new CopyOnWriteArrayList<>(routeDto.getTargets()));
    }

    public List<TargetDto> getTargetsOfRoute(RouteDto routeDto) {
        return targets.getOrDefault(routeDto.getPath(), new ArrayList<>());
    }

    public Map<String, List<TargetDto>> getAllTargets() {
        return targets;
    }
}