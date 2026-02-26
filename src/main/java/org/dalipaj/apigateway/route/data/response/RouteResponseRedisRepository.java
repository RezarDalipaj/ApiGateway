package org.dalipaj.apigateway.route.data.response;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RouteResponseRedisRepository {

    @Value("${app.response-ttl-minutes}")
    private Long responseTtlMinutes;
    private final RedisTemplate<String, RouteRedisResponseWithMetadata> responseRedisTemplate;
    private final IHashService hashService;
    public static final String ROUTE_KEY_PREFIX = "route:";

    public void save(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata)
            throws NoSuchAlgorithmException {
        responseRedisTemplate.opsForValue().set(
                ROUTE_KEY_PREFIX + hashService.sha256(routeRedisResponseWithMetadata.getKey()),
                routeRedisResponseWithMetadata,
                responseTtlMinutes,
                TimeUnit.MINUTES);
    }

    public RouteRedisResponseWithMetadata get(RouteResponseKey key) throws NoSuchAlgorithmException {
        return responseRedisTemplate.opsForValue().get(ROUTE_KEY_PREFIX + hashService.sha256(key));
    }

}
