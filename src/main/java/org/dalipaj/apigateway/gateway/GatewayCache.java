package org.dalipaj.apigateway.gateway;

import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.rateLimit.RateLimitDto;
import org.dalipaj.apigateway.route.dto.RouteTree;
import org.dalipaj.apigateway.route.RouteUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public class GatewayCache {

    private Map<String, RouteTree> routeTrees = new HashMap<>();
    private Map<String, RateLimitDto> rateLimits = new HashMap<>();

    public RouteTree getRouteTree(String fullPath) {
        var mainPath = RouteUtil.getMainPath(fullPath);
        return getRouteTrees().get(mainPath);
    }
}
