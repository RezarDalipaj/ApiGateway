package org.dalipaj.apigateway.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OAuth {
    private String tokenEndpoint;      // https://auth/token
    private String clientId;
    private String clientSecret;
    private String scope;              // optional
}
