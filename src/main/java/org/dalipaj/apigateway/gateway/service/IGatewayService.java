package org.dalipaj.apigateway.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.route.response.RouteRedisResponseDto;

public interface IGatewayService {

    RouteRedisResponseDto routeAndPrepareResponse(HttpServletRequest req);
}
