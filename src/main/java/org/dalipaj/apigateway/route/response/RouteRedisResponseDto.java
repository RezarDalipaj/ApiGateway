package org.dalipaj.apigateway.route.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@Setter
public class RouteRedisResponseDto {

    private HttpStatus status;
    private Object data;
    private HttpHeaders headers;
}
