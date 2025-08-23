package org.dalipaj.apigateway.route.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OAuthDto {

    @JsonIgnore
    private Long id;

    @NotEmpty
    private String tokenEndpoint;

    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientSecret;

    private String scope;
}
