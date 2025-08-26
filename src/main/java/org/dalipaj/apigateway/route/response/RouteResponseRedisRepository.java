package org.dalipaj.apigateway.route.response;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RouteResponseRedisRepository {

    private final RedisTemplate<String, RouteRedisResponseWithMetadata> responseRedisTemplate;
    public static final String ROUTE_KEY_PREFIX = "route:";

    public void save(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata) {
        responseRedisTemplate.opsForValue().set(ROUTE_KEY_PREFIX + routeRedisResponseWithMetadata.getExactPath(), routeRedisResponseWithMetadata);
    }

    public RouteRedisResponseWithMetadata get(String path) {
        return responseRedisTemplate.opsForValue().get(ROUTE_KEY_PREFIX + path);
    }

}
