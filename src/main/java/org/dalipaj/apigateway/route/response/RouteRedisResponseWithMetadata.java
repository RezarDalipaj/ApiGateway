package org.dalipaj.apigateway.route.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class RouteRedisResponseWithMetadata {

    private RouteResponseDto response;
    private LocalDateTime lastCached;
    private String exactPath;
}
