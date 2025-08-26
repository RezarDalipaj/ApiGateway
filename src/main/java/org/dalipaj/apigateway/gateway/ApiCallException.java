package org.dalipaj.apigateway.gateway;

import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiCallException extends RuntimeException {

    private HttpStatus status;

    private RouteRedisResponseWithMetadata responseWithMetadata;

    public ApiCallException(String message, HttpStatus statusCode,
                            RouteRedisResponseWithMetadata responseWithMetadata) {
        super(message);
        setStatus(statusCode);
        setResponseWithMetadata(responseWithMetadata);
    }
}
