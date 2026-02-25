package org.dalipaj.apigateway.loadBalancer.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategy;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeastConnectionsStrategy implements LoadBalancerStrategy {

    @Override
    public TargetDto chooseTarget(List<TargetDto> targetDtos, String clientIp) {
        return targetDtos.stream()
                .filter(TargetDto::isHealthy)
                .min(Comparator.comparingInt(h -> h.getActiveConnections().get()))
                .orElseThrow(() -> new NullPointerException(NO_HEALTHY_UPSTREAMS));
    }
}
