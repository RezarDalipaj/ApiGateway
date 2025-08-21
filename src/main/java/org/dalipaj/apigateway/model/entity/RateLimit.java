package org.dalipaj.apigateway.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateLimit {
    private String apiKey;             // per-key (or "*" for default)
    private Integer perMinute;         // null means unlimited
    private Integer perHour;           // null means unlimited
}