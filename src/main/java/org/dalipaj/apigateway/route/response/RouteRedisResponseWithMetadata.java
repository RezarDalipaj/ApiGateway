package org.dalipaj.apigateway.route.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RouteRedisResponseWithMetadata implements Serializable {

    private RouteResponseDto response;
    private LocalDateTime lastCached;
    private String exactPath;
}
