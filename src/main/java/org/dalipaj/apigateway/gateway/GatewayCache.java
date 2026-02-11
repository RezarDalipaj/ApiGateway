package org.dalipaj.apigateway.gateway;

import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.rateLimit.RateLimitDto;
import org.dalipaj.apigateway.route.dto.RouteTrie;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@Setter
public class GatewayCache {

    private RouteTrie routeTrie;
    private Map<String, RateLimitDto> rateLimits = new HashMap<>();
}