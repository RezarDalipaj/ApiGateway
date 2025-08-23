package org.dalipaj.apigateway.gateway;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.gateway.service.IGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final IGatewayService gatewayService;

    @RequestMapping("/**")
    public ResponseEntity<Object> handle(HttpServletRequest req) {
        var response = gatewayService.routeAndPrepareResponse(req);

        return ResponseEntity.status(response.getStatus())
                .headers(response.getHeaders())
                .body(response.getData());
    }
}
