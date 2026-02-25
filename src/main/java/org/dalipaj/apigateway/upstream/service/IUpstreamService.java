package org.dalipaj.apigateway.upstream.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.filter.FilterDto;
import org.dalipaj.apigateway.route.data.RouteDto;
import org.dalipaj.apigateway.route.data.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
import org.dalipaj.apigateway.upstream.data.service.ServiceDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface IUpstreamService {

    ServiceDto save(ServiceDto serviceDto,
                    HttpServletRequest request) throws UnAuthorizedException;

    RouteDto getRouteForRequest(String path);

    List<TargetDto> getTargets(RouteDto routeDto);

    @Transactional
    void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata, HttpMethod httpMethod);

    RouteRedisResponseWithMetadata getRouteResponseFromCache(String path, HttpMethod httpMethod);

    @Transactional
    void delete(Long id,
                HttpServletRequest request) throws UnAuthorizedException;

    ServiceDto getById(Long id, HttpServletRequest request) throws UnAuthorizedException;

    Page<ServiceDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters);
}
