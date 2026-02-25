package org.dalipaj.apigateway.loadBalancer.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategy;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
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
    public TargetDto chooseTarget(List<TargetDto> targetDtos, String clientIp) {
        var healthyTargets = targetDtos.stream()
                .filter(TargetDto::isHealthy)
                .toList();

        int totalWeight = healthyTargets.stream()
                .mapToInt(TargetDto::getEffectiveWeight)
                .sum();

        if (totalWeight == 0) {
            throw new RuntimeException(NO_HEALTHY_UPSTREAMS);
        }

        AtomicInteger counter = counters.computeIfAbsent(
                "global", k -> new AtomicInteger(0));

        int index = Math.abs(counter.incrementAndGet()) % totalWeight;

        int cumulative = 0;
        for (var target : healthyTargets) {
            cumulative += target.getEffectiveWeight();
            if (index < cumulative) {
                return target;
            }
        }

        return healthyTargets.getFirst();
    }
}
