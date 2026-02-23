package org.dalipaj.apigateway.loadBalancer.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategy;
import org.dalipaj.apigateway.upstream.backend.BackendDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IpHashStrategy implements LoadBalancerStrategy {

    @Override
    public BackendDto chooseBackend(List<BackendDto> backends, String clientIp) {
        var healthy = backends.stream()
                .filter(BackendDto::isHealthy)
                .toList();

        int index = Math.abs(clientIp.hashCode()) % healthy.size();
        return healthy.get(index);
    }
}
