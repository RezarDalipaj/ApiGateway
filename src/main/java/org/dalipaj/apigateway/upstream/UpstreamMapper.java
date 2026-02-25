package org.dalipaj.apigateway.upstream;

import org.dalipaj.apigateway.route.data.RouteDto;
import org.dalipaj.apigateway.route.data.RouteEntity;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
import org.dalipaj.apigateway.upstream.data.target.TargetEntity;
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

    @Mapping(source = "application.username", target = "applicationName")
    ServiceDto toServiceDto(ServiceEntity service);

    @Mapping(source = "loadBalancerType", target = "loadBalancerType", defaultValue = "LATENCY")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "targets", ignore = true)
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

    @Mapping(source = "healthCheckPath", target = "healthCheckPath", defaultValue = "/")
    TargetDto toTargetDto(TargetEntity targetEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "routes", ignore = true)
    @Mapping(source = "healthCheckPath", target = "healthCheckPath", defaultValue = "/")
    TargetEntity toTargetEntity(TargetDto targetDto);

    List<TargetEntity> toTargetEntityList(List<TargetDto> targetDtos);
}
