package org.dalipaj.apigateway.repository.redis;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.model.entity.ApiKey;
import org.dalipaj.apigateway.model.entity.Backend;
import org.dalipaj.apigateway.model.entity.RateLimit;
import org.dalipaj.apigateway.model.entity.Route;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, ApiKey> apiKeyRedisTemplate;
    private final RedisTemplate<String, RateLimit> rateLimitRedisTemplate;
    private final RedisTemplate<String, Backend> backendRedisTemplate;
    private final  RedisTemplate<String, Route> routeRedisTemplate;

    public void saveApiKey(ApiKey apiKey) {
        apiKeyRedisTemplate.opsForValue().set(apiKey.getKey(), apiKey);
    }

    public ApiKey getApiKey(String apiKey) {
        return apiKeyRedisTemplate.opsForValue().get(apiKey);
    }

    public void saveRoute(Route route) {
        routeRedisTemplate.opsForValue().set(route.getPathPattern(), route);
    }

    public Route getRoute(String pathPattern) {
        return routeRedisTemplate.opsForValue().get(pathPattern);
    }

    public void saveRateLimit(RateLimit rateLimit) {
        rateLimitRedisTemplate.opsForValue().set(rateLimit.getApiKey(), rateLimit);
    }

    public RateLimit getRateLimit(String keyValue) {
        return rateLimitRedisTemplate.opsForValue().get(keyValue);
    }

    public void saveBackend(Backend backend) {
        backendRedisTemplate.opsForValue().set(backend.getName(), backend);
    }

    public Backend getBackend(String name) {
        return backendRedisTemplate.opsForValue().get(name);
    }

}
