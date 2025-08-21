package org.dalipaj.apigateway.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.service.business.IGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final IGatewayService gatewayService;

    @RequestMapping("/**")
    public ResponseEntity<String> handle(HttpServletRequest req) {
        var route = gatewayService.getRouteForRequest(req);

        // (from here do: API key / rate limit / cache / load-balance / proxy ...)
        // For demo purposes:
        return ResponseEntity.ok("Matched route " + route.getId() + " for " + req.getMethod() + " " + req.getRequestURI());
    }
}
