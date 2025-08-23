package org.dalipaj.apigateway.route.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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

    @NotEmpty
    private List<BackendDto> backends;

    private OAuthDto oauth;
}
