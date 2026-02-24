package org.dalipaj.apigateway.gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.rateLimit.service.RateLimitException;
import org.dalipaj.apigateway.route.data.response.RouteResponseDto;
import org.springframework.validation.annotation.Validated;

import java.security.NoSuchAlgorithmException;

@Validated
public interface IGatewayService {

    RouteResponseDto routeAndPrepareResponse(HttpServletRequest req, Object requestBody) throws RateLimitException, NoSuchAlgorithmException;
}
