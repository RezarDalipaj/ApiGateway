package org.dalipaj.apigateway.loadBalancer;

public enum LoadBalancerType {
    ROUND_ROBIN,
    IP_HASH,
    CONNECTIONS,
    LATENCY
}
