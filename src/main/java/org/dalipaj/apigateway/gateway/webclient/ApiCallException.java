package org.dalipaj.apigateway.gateway.webclient;

import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.route.data.response.RouteRedisResponseWithMetadata;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiCallException extends RuntimeException {

    private HttpStatus status;

    private HttpMethod httpMethod;

    private RouteRedisResponseWithMetadata responseWithMetadata;

    public ApiCallException(String message, HttpStatus statusCode,
                            HttpMethod httpMethod,
                            RouteRedisResponseWithMetadata responseWithMetadata) {
        super(message);
        setStatus(statusCode);
        setHttpMethod(httpMethod);
        setResponseWithMetadata(responseWithMetadata);
    }
}
