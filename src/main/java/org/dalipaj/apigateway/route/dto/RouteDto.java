package org.dalipaj.apigateway.route.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.loadBalancer.LoadBalancerType;
import org.dalipaj.apigateway.route.backend.BackendDto;
import org.dalipaj.apigateway.route.oauth.OAuthDto;
import org.dalipaj.apigateway.route.RouteAuthType;

import java.util.List;

@Builder
@Getter
@Setter
public class RouteDto {

    @NotBlank
    private String path;

    private Boolean stripPrefix;

    private RouteAuthType authType;

    private LoadBalancerType loadBalancerType;

    @NotEmpty
    private List<BackendDto> backends;

    private OAuthDto oauth;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;
}
