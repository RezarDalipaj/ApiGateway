package org.dalipaj.apigateway.loadBalancer.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategy;
import org.dalipaj.apigateway.upstream.data.backend.BackendDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class RoundRobinStrategy implements LoadBalancerStrategy {

    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    @Override
    public BackendDto chooseBackend(List<BackendDto> backends, String clientIp) {
        var healthyBackends = backends.stream()
                .filter(BackendDto::isHealthy)
                .toList();

        int totalWeight = healthyBackends.stream()
                .mapToInt(BackendDto::getEffectiveWeight)
                .sum();

        if (totalWeight == 0) {
            throw new RuntimeException(NO_HEALTHY_UPSTREAMS);
        }

        AtomicInteger counter = counters.computeIfAbsent(
                "global", k -> new AtomicInteger(0));

        int index = Math.abs(counter.incrementAndGet()) % totalWeight;

        int cumulative = 0;
        for (var backend : healthyBackends) {
            cumulative += backend.getEffectiveWeight();
            if (index < cumulative) {
                return backend;
            }
        }

        return healthyBackends.getFirst();
    }
}
