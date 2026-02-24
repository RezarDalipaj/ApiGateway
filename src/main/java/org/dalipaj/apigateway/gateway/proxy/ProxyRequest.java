package org.dalipaj.apigateway.gateway.proxy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.upstream.data.backend.BackendDto;

@Builder
@Getter
@Setter
public class ProxyRequest {

    private BackendDto backend;
    private HttpServletRequest httpRequest;
    private Object requestBody;
}
