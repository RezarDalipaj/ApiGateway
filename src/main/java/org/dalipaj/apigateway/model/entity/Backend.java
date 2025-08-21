package org.dalipaj.apigateway.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Backend {
    private String name;               // unique
    private String url;                // http://service:8080
    private Integer weight;            // default 1
    private boolean enabled;           // default true
}

