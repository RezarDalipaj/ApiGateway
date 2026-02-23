package org.dalipaj.apigateway.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final WebClientExceptionHandler exceptionHandler;

    /**
     * Web client bean config for apigee api calls
     *
     * @return WebClient.Builder
     */
    @Bean(name = "apigeeWebClient")
    public WebClient apigeeWebClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .filter(exceptionHandler.createResponseErrorHandler())
                .build();
    }
}
