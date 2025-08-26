package org.dalipaj.apigateway.route;

import jakarta.validation.constraints.NotEmpty;
import org.dalipaj.apigateway.route.backend.BackendDto;
import org.dalipaj.apigateway.route.oauth.OAuthDto;
import org.dalipaj.apigateway.route.backend.BackendEntity;
import org.dalipaj.apigateway.route.oauth.OAuthEntity;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface RouteMapper {

    @Mapping(source = "authType", target = "authType", defaultValue = "NONE")
    @Mapping(source = "loadBalancerType", target = "loadBalancerType", defaultValue = "ROUND_ROBIN")
    @Mapping(target = "user", ignore = true)
    RouteEntity toEntity(RouteDto routeDto);

    @Mapping(source = "user.username", target = "username")
    RouteDto toDto(RouteEntity route);

    OAuthDto toOAuthDto(OAuthEntity oauth);

    @Mapping(target = "routes", ignore = true)
    OAuthEntity toOAuth(OAuthDto oauthDto);

    BackendDto toBackendDto(BackendEntity backend);

    @Mapping(target = "routes", ignore = true)
    BackendEntity toBackend(BackendDto backendDto);

    List<BackendEntity> toBackends(@NotEmpty List<BackendDto> backends);
}
