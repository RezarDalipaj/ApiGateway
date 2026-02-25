package org.dalipaj.apigateway.loadBalancer.strategy;

import org.dalipaj.apigateway.upstream.data.target.TargetDto;

import java.util.List;

public interface LoadBalancerStrategy {

    String NO_HEALTHY_UPSTREAMS = "No healthy upstreams";

    TargetDto chooseTarget(List<TargetDto> targetDtos, String clientIp);
}
