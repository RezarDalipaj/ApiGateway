package org.dalipaj.apigateway.upstream.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.filter.FilterDto;
import org.dalipaj.apigateway.common.pagination.PaginationService;
import org.dalipaj.apigateway.gateway.localcache.GatewayCache;
import org.dalipaj.apigateway.route.data.RouteDto;
import org.dalipaj.apigateway.route.data.RouteEntity;
import org.dalipaj.apigateway.route.data.RouteRepository;
import org.dalipaj.apigateway.route.data.RouteTrie;
import org.dalipaj.apigateway.route.data.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.data.response.RouteResponseKey;
import org.dalipaj.apigateway.route.data.response.RouteResponseRedisRepository;
import org.dalipaj.apigateway.upstream.UpstreamMapper;
import org.dalipaj.apigateway.upstream.data.service.ServiceDto;
import org.dalipaj.apigateway.upstream.data.service.ServiceEntity;
import org.dalipaj.apigateway.upstream.data.service.ServiceRepository;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
import org.dalipaj.apigateway.upstream.service.IUpstreamService;
import org.dalipaj.apigateway.upstream.service.IUpstreamTransactionalService;
import org.dalipaj.apigateway.user.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpstreamService extends PaginationService implements IUpstreamService {

    private final IUpstreamTransactionalService upstreamTransactionalService;
    private final RouteRepository routeRepository;
    private final ServiceRepository serviceRepository;
    private final UpstreamMapper upstreamMapper;
    private final GatewayCache gatewayCache;
    private final RouteResponseRedisRepository routeResponseRedisRepository;
    private final IUserService userService;

    private static final String GET_METHOD = HttpMethod.GET.toString();

    @PostConstruct
    void initInMemoryRoutes() {
        gatewayCache.setRouteTrie(new RouteTrie());

        for (RouteEntity route : routeRepository.findAll()) {
            var routeDto = upstreamMapper.toRouteDto(route);
            gatewayCache.getRouteTrie().insert(routeDto);
            gatewayCache.addRouteTargets(routeDto);
        }
    }

    @Override
    public ServiceDto save(ServiceDto serviceDto,
                           HttpServletRequest request) throws UnAuthorizedException {
        var entityId = upstreamTransactionalService.saveEntity(serviceDto, request);
        var entity = upstreamTransactionalService.findById(entityId);

        serviceDto = upstreamMapper.toServiceDto(entity);

        for (var route : serviceDto.getRoutes()) {
            gatewayCache.getRouteTrie().insert(route);
            gatewayCache.addRouteTargets(route);
        }

        return serviceDto;
    }

    @Override
    public RouteDto getRouteForRequest(String path) {
        return gatewayCache.getRouteTrie().match(path)
                .orElseThrow(() -> new NullPointerException("Route with path: " + path + " not found"));
    }

    @Override
    public List<TargetDto> getTargets(RouteDto routeDto) {
        return gatewayCache.getTargetsOfRoute(routeDto);
    }

    @Override
    public void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata) throws NoSuchAlgorithmException {
        var exactPath = routeRedisResponseWithMetadata.getKey().getExactPath();
        var httpMethod = routeRedisResponseWithMetadata.getKey().getHttpMethod();

        if (httpMethod.equals(GET_METHOD)) {
            log.info("Saving response for GET request on path: {} in redis", exactPath);
            routeResponseRedisRepository.save(routeRedisResponseWithMetadata);
        }
        else
            log.info("Not saving response for {} request on path: {} in redis, only GET responses are cached",
                    httpMethod, exactPath);
    }

    @Override
    public RouteRedisResponseWithMetadata getRouteResponseFromCache(RouteResponseKey key) throws NoSuchAlgorithmException {
        var path = key.getExactPath();
        var httpMethod = key.getHttpMethod();

        if (!httpMethod.equals(GET_METHOD)) {
            log.info("Not fetching response for {} request on path: {} from redis, only GET responses are cached",
                    httpMethod, path);
            return null;
        }

        return routeResponseRedisRepository.get(key);
    }

    @Override
    public void delete(Long id,
                       HttpServletRequest request) throws UnAuthorizedException {
        var entity = checkAppPermissionsAndGetEntity(id, request);
        upstreamTransactionalService.deleteRouteFromTargets(entity.getId());

        serviceRepository.delete(entity);

        for (var route : entity.getRoutes()) {
            var deleted = gatewayCache.getRouteTrie().delete(route.getPath());
            log.info("Route {} was {} deleted from in memory", id, deleted ? "" : "not");
        }

    }

    @Override
    public ServiceDto getById(Long id,
                              HttpServletRequest request) throws UnAuthorizedException {
        var entity = checkAppPermissionsAndGetEntity(id, request);
        return upstreamMapper.toServiceDto(entity);
    }

    private ServiceEntity checkAppPermissionsAndGetEntity(Long id,
                                                          HttpServletRequest request) throws UnAuthorizedException {
        var entity = upstreamTransactionalService.findById(id);
        userService.checkUserPermissions(request, entity.getApplication().getUsername());

        return entity;
    }

    @Override
    public Page<ServiceDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters) {
        var entityPages = super.getAll(pageNumber, pageSize, filters, serviceRepository);

        return entityPages.map(upstreamMapper::toServiceDto);
    }
}
