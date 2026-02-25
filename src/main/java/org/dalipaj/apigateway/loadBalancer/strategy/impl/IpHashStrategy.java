package org.dalipaj.apigateway.loadBalancer.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerStrategy;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IpHashStrategy implements LoadBalancerStrategy {

    @Override
    public TargetDto chooseTarget(List<TargetDto> targetDtos, String clientIp) {
        var healthy = targetDtos.stream()
                .filter(TargetDto::isHealthy)
                .toList();

        int index = Math.abs(clientIp.hashCode()) % healthy.size();
        return healthy.get(index);
    }
}
