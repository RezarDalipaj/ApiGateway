package org.dalipaj.apigateway.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.upstream.backend.BackendDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;

public interface IProxyService {

    RouteRedisResponseWithMetadata proxyRequest(HttpServletRequest request,
                                                Object requestBody,
                                                BackendDto backend);
}
