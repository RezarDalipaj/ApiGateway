package org.dalipaj.apigateway.route.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.common.validation.OnUpdateGroup;
import org.dalipaj.apigateway.loadBalancer.strategy.LoadBalancerType;
import org.dalipaj.apigateway.route.data.oauth.OAuthDto;
import org.dalipaj.apigateway.upstream.data.backend.BackendDto;

import java.util.List;

@Builder
@Getter
@Setter
public class RouteDto {

    @NotBlank(groups = OnCreateGroup.class)
    @Pattern(regexp = "^/.*", message = "Route path must start with '/'",
            groups = {OnCreateGroup.class, OnUpdateGroup.class})
    private String path;

    private LoadBalancerType loadBalancerType;

    @NotEmpty(groups = OnCreateGroup.class)
    @Valid
    private List<BackendDto> backends;

    private OAuthDto oauth;
}
