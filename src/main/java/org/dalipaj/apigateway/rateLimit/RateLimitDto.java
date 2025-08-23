package org.dalipaj.apigateway.rateLimit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RateLimitDto {

    private Long id;
    private Integer perMinute;
    private Integer perHour;
    private String apiKey;
}
