package org.dalipaj.apigateway.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.rateLimit.RateLimitException;
import org.dalipaj.apigateway.route.response.RouteResponseDto;

public interface IGatewayService {

    RouteResponseDto routeAndPrepareResponse(HttpServletRequest req, Object requestBody) throws RateLimitException;
}
