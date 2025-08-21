package org.dalipaj.apigateway.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dalipaj.apigateway.model.enumeration.AuthType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    private String id;                 // route id (key suffix)
    private String pathPattern;        // "/users/{id}", "/orders/**"
    private String httpMethod;         // GET/POST/PUT/DELETE or "*"
    private boolean stripPrefix = true;
    private boolean enabled = true;
    private AuthType authType;

    // caching
    private Integer cacheTtlSeconds;   // null/0 means no caching

    // load balancing
    private List<String> backendNames; // e.g. ["user-a","user-b"]

    // oauth to upstream (optional)
    private OAuth oauth; // nullable
}

