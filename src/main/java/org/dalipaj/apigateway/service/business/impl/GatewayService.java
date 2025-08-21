package org.dalipaj.apigateway.service.business.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.model.entity.Route;
import org.dalipaj.apigateway.repository.redis.RouterManager;
import org.dalipaj.apigateway.service.business.IGatewayService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayService implements IGatewayService {

    private final RouterManager routerManager;

    @Override
    public Route getRouteForRequest(HttpServletRequest req) {
        String method = req.getMethod();
        String path   = req.getRequestURI();

        var matchResult = routerManager.getRouter().match(method, path);
        if (!matchResult.found)
            throw new NullPointerException("No route for path: " + path + " and method: " + method);

        return matchResult.route;
    }
}
