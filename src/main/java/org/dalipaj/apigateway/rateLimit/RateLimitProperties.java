package org.dalipaj.apigateway.rateLimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.rate-limit")
@Getter
@Setter
public class RateLimitProperties {

    private Long perMinute;
    private Long perHour;
}
