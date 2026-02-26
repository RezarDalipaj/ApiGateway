package org.dalipaj.apigateway.gateway.proxy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.route.data.response.RouteResponseKey;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;

@Builder
@Getter
@Setter
public class ProxyRequest {

    private TargetDto target;
    private HttpServletRequest httpRequest;
    private RouteResponseKey redisKey;
    private Object requestBody;
}
