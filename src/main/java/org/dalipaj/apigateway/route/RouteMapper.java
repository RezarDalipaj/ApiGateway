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
    RouteEntity toEntity(RouteDto routeDto);

    RouteDto toDto(RouteEntity route);

    OAuthDto toOAuthDto(OAuthEntity oauth);

    OAuthEntity toOAuth(OAuthDto oauthDto);

    BackendDto toBackendDto(BackendEntity backend);

    BackendEntity toBackend(BackendDto backendDto);

    List<BackendEntity> toBackends(@NotEmpty List<BackendDto> backends);
}
