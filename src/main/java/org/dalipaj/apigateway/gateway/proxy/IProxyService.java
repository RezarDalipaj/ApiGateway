package org.dalipaj.apigateway.gateway.proxy;

import org.dalipaj.apigateway.route.data.response.RouteRedisResponseWithMetadata;

public interface IProxyService {

    RouteRedisResponseWithMetadata proxyRequest(ProxyRequest proxyRequest);
}
