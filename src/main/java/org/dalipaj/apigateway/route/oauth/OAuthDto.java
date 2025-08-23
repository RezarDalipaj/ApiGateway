package org.dalipaj.apigateway.route.oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OAuthDto {

    private Long id;
    private String tokenEndpoint;
    private String clientId;
    private String clientSecret;
    private String scope;
}
