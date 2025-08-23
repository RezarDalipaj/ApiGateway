package org.dalipaj.apigateway.route.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
final class RouteNode {
    private RouteDto route;  // optional, if this node is an endpoint
    private final Map<String, RouteNode> children = new HashMap<>();
}
