package org.dalipaj.apigateway.route.data.oauth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;

@Builder
@Getter
@Setter
public class OAuthDto {

    @JsonIgnore
    private Long id;

    @NotEmpty(groups = OnCreateGroup.class)
    private String name;

    @NotEmpty(groups = OnCreateGroup.class)
    private String tokenEndpoint;

    @NotEmpty(groups = OnCreateGroup.class)
    private String clientId;

    @NotEmpty(groups = OnCreateGroup.class)
    private String clientSecret;

    private String scope;
}
