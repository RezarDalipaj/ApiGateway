package org.dalipaj.apigateway.loadBalancer.strategy;

public enum LoadBalancerType {
    ROUND_ROBIN,
    IP_HASH,
    CONNECTIONS,
    LATENCY
}
