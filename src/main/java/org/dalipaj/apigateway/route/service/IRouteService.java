package org.dalipaj.apigateway.route.service;

import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface IRouteService {

    @Transactional
    RouteDto save(RouteDto routeDto);

    RouteDto getRouteForRequest(String path, String method);

    @Transactional
    void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata);

    RouteRedisResponseWithMetadata getRouteResponseFromCache(String path);

    @Transactional
    void delete(String path);

    RouteDto getByPath(String path);

    Page<RouteDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters);
}
