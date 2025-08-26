package org.dalipaj.apigateway.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.route.backend.BackendDto;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;

public interface IProxyService {

    RouteRedisResponseWithMetadata proxyRequest(HttpServletRequest request, Object requestBody,
                                                RouteDto route, BackendDto backend);
}
