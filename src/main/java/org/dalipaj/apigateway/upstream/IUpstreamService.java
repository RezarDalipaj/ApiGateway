package org.dalipaj.apigateway.upstream;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.upstream.backend.BackendDto;
import org.dalipaj.apigateway.upstream.service.ServiceDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface IUpstreamService {

    ServiceDto save(ServiceDto serviceDto, HttpServletRequest request) throws UnAuthorizedException;

    RouteDto getRouteForRequest(String path);

    List<BackendDto> getBackends(RouteDto routeDto);

    @Transactional
    void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata, HttpMethod httpMethod);

    RouteRedisResponseWithMetadata getRouteResponseFromCache(String path, HttpMethod httpMethod);

    @Transactional
    void delete(Long id, HttpServletRequest request) throws UnAuthorizedException;

    ServiceDto getById(Long id, HttpServletRequest request) throws UnAuthorizedException;

    Page<ServiceDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters);
}
