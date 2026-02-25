package org.dalipaj.apigateway.upstream.data.target;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class TargetDto implements Serializable {

    @NotEmpty(groups = OnCreateGroup.class)
    private final String host;

    private final String healthCheckPath;

    @NotNull(groups = OnCreateGroup.class)
    private final Integer weight;

    private static final double ALPHA = 0.2;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    private volatile double avgLatency = 0;
    private volatile boolean healthy = true;
    private volatile int effectiveWeight;

    public TargetDto(String host, String healthCheckPath, int weight) {
        this.host = host;
        this.healthCheckPath = healthCheckPath;
        this.weight = weight;
        this.effectiveWeight = weight;
    }

    public void incrementConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementConnections() {
        activeConnections.decrementAndGet();
    }

    public void updateLatency(long newLatency) {

        if (avgLatency == 0) {
            avgLatency = newLatency;
        } else {
            var latency = (avgLatency * (1 - ALPHA));
            avgLatency = latency + (newLatency * ALPHA);
        }

        adjustWeightBasedOnLatency();
    }

    private void adjustWeightBasedOnLatency() {

        if (!healthy) {
            effectiveWeight = 0;
            return;
        }

        // Simple penalty: higher latency reduces effective weight
        double penaltyFactor = 1 / (1 + avgLatency / 100.0);

        effectiveWeight = (int) Math.max(1, weight * penaltyFactor);
    }

    public void markHealthy(boolean healthy) { this.healthy = healthy; }
}

