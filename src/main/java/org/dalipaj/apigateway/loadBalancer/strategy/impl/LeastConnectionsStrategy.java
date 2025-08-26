package org.dalipaj.apigateway.loadBalancer.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategy;
import org.dalipaj.apigateway.route.backend.BackendDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LeastConnectionsStrategy implements LoadBalancerStrategy {

    @Override
    public BackendDto chooseBackend(List<BackendDto> backends) {
        return null;
    }
}
