package org.dalipaj.apigateway.loadBalancer.strategy;

import org.dalipaj.apigateway.upstream.backend.BackendDto;

import java.util.List;

public interface LoadBalancerStrategy {

    BackendDto chooseBackend(List<BackendDto> backends, String clientIp);
}
