package org.dalipaj.apigateway.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.rateLimit.RateLimitException;
import org.dalipaj.apigateway.route.response.RouteResponseDto;
import org.springframework.validation.annotation.Validated;

import java.security.NoSuchAlgorithmException;

@Validated
public interface IGatewayService {

    RouteResponseDto routeAndPrepareResponse(HttpServletRequest req, Object requestBody) throws RateLimitException, NoSuchAlgorithmException;
}
