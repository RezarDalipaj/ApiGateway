package org.dalipaj.apigateway.service.business;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.model.entity.Route;

public interface IGatewayService {

    Route getRouteForRequest(HttpServletRequest req);
}
