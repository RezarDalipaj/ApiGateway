package org.dalipaj.apigateway.loadBalancer.strategy;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.loadBalancer.LoadBalancerType;
import org.dalipaj.apigateway.loadBalancer.strategy.impl.IpHashStrategy;
import org.dalipaj.apigateway.loadBalancer.strategy.impl.LatencyStrategy;
import org.dalipaj.apigateway.loadBalancer.strategy.impl.LeastConnectionsStrategy;
import org.dalipaj.apigateway.loadBalancer.strategy.impl.RoundRobinStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadBalancerStrategyFactory {

    private final RoundRobinStrategy roundRobinStrategy;
    private final LatencyStrategy latencyStrategy;
    private final LeastConnectionsStrategy leastConnectionsStrategy;
    private final IpHashStrategy ipHashStrategy;

    public LoadBalancerStrategy getStrategy(LoadBalancerType type) {
        if (type == null)
            return roundRobinStrategy;

        return switch (type) {
            case LATENCY -> latencyStrategy;
            case CONNECTIONS -> leastConnectionsStrategy;
            case IP_HASH -> ipHashStrategy;
            case ROUND_ROBIN -> roundRobinStrategy;
        };
    }
}
