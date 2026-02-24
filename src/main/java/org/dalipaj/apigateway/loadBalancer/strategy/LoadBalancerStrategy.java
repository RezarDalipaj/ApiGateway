package org.dalipaj.apigateway.loadBalancer.strategy;

import org.dalipaj.apigateway.upstream.data.backend.BackendDto;

import java.util.List;

public interface LoadBalancerStrategy {

    String NO_HEALTHY_UPSTREAMS = "No healthy upstreams";

    BackendDto chooseBackend(List<BackendDto> backends, String clientIp);
}
