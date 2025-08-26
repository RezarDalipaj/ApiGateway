package org.dalipaj.apigateway.route.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface IRouteService {

    @Transactional
    RouteDto save(RouteDto routeDto, HttpServletRequest request) throws UnAuthorizedException;

    RouteDto getRouteForRequest(String path);

    @Transactional
    void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata);

    RouteRedisResponseWithMetadata getRouteResponseFromCache(String path);

    @Transactional
    void delete(String path, HttpServletRequest request) throws UnAuthorizedException;

    RouteDto getByPath(String path, HttpServletRequest request) throws UnAuthorizedException;

    Page<RouteDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters);
}
