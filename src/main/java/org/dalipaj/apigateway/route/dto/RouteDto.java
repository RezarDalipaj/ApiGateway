package org.dalipaj.apigateway.route.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.common.validation.OnUpdateGroup;
import org.dalipaj.apigateway.loadBalancer.LoadBalancerType;
import org.dalipaj.apigateway.route.RouteAuthType;
import org.dalipaj.apigateway.route.oauth.OAuthDto;
import org.dalipaj.apigateway.upstream.backend.BackendDto;

import java.util.List;

@Builder
@Getter
@Setter
public class RouteDto {

    @NotBlank(groups = OnCreateGroup.class)
    @Pattern(regexp = "^/.*", message = "Route path must start with '/'",
            groups = {OnCreateGroup.class, OnUpdateGroup.class})
    private String path;

    private RouteAuthType authType;

    private LoadBalancerType loadBalancerType;

    @NotEmpty(groups = OnCreateGroup.class)
    @Valid
    private List<BackendDto> backends;

    private OAuthDto oauth;
}
