package org.dalipaj.apigateway.loadBalancer.healthcheck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.gateway.localcache.GatewayCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthChecker {

    private final GatewayCache gatewayCache;
    private final WebClient webClient;

    @Scheduled(fixedDelayString = "${app.healthCheckJobSeconds}",
            timeUnit = TimeUnit.SECONDS)
    public void check() {
        gatewayCache.getAllUpstreams().values().forEach(targets ->
            targets.forEach(target -> {
                try {
                    log.info("Checking route health check for {}", target.getHost());
                    webClient.get()
                            .uri(target.getHost() + target.getHealthCheckPath())
                            .retrieve()
                            .toBodilessEntity()
                            .block(Duration.ofSeconds(3));

                    target.markHealthy(true);
                    log.info("Route {} is healthy", target.getHost());

                } catch (Exception e) {
                    log.warn("Route {} is unhealthy", target.getHost(), e);
                    target.markHealthy(false);
                }
            })
        );
    }
}

