package org.dalipaj.apigateway.loadBalancer.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategy;
import org.dalipaj.apigateway.upstream.data.backend.BackendDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeastConnectionsStrategy implements LoadBalancerStrategy {

    @Override
    public BackendDto chooseBackend(List<BackendDto> backends, String clientIp) {
        return backends.stream()
                .filter(BackendDto::isHealthy)
                .min(Comparator.comparingInt(h -> h.getActiveConnections().get()))
                .orElseThrow(() -> new NullPointerException(NO_HEALTHY_UPSTREAMS));
    }
}
