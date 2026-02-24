package org.dalipaj.apigateway.gateway;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.rateLimit.service.RateLimitException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final IGatewayService gatewayService;

    @RequestMapping("/**")
    public ResponseEntity<Object> handle(HttpServletRequest req,
                                         @RequestBody(required = false) Object requestBody)
            throws RateLimitException, NoSuchAlgorithmException {

        var response = gatewayService.routeAndPrepareResponse(req, requestBody);

        return ResponseEntity.status(response.getStatus())
                .headers(response.getHeaders())
                .body(response.getData());
    }
}
