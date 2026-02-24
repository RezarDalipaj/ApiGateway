package org.dalipaj.apigateway.upstream;

import org.dalipaj.apigateway.route.data.RouteEntity;
import org.dalipaj.apigateway.route.data.RouteDto;
import org.dalipaj.apigateway.route.data.oauth.OAuthDto;
import org.dalipaj.apigateway.route.data.oauth.OAuthEntity;
import org.dalipaj.apigateway.upstream.data.backend.BackendDto;
import org.dalipaj.apigateway.upstream.data.backend.BackendEntity;
import org.dalipaj.apigateway.upstream.data.service.ServiceDto;
import org.dalipaj.apigateway.upstream.data.service.ServiceEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface UpstreamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "routes", ignore = true)
    ServiceEntity toServiceEntity(ServiceDto serviceDto);

    @Mapping(source = "application.name", target = "applicationName")
    ServiceDto toServiceDto(ServiceEntity service);

    @Mapping(source = "loadBalancerType", target = "loadBalancerType", defaultValue = "LATENCY")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "backends", ignore = true)
    RouteEntity toRouteEntity(RouteDto routeDto);

    List<RouteEntity> toRouteEntityList(List<RouteDto> routeDtos);

    default List<RouteEntity> toRouteEntityList(String serviceName, List<RouteDto> routeDtos) {
        List<RouteEntity> routes = toRouteEntityList(routeDtos);

        return routes.stream()
                .peek(route ->
                        route.setPath(serviceName + route.getPath()))
                .toList();
    }

    RouteDto toRouteDto(RouteEntity route);

    OAuthDto toOAuthDto(OAuthEntity oauth);

    @Mapping(target = "routes", ignore = true)
    @Mapping(target = "id", ignore = true)
    OAuthEntity toOAuthEntity(OAuthDto oauthDto);

    @Mapping(source = "healthCheckPath", target = "healthCheckPath", defaultValue = "/")
    BackendDto toBackendDto(BackendEntity backend);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "routes", ignore = true)
    @Mapping(source = "healthCheckPath", target = "healthCheckPath", defaultValue = "/")
    BackendEntity toBackendEntity(BackendDto backendDto);

    List<BackendEntity> toBackendEntityList(List<BackendDto> backendDtos);
}
