package org.dalipaj.apigateway.upstream;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.application.service.IApplicationService;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.PaginationService;
import org.dalipaj.apigateway.gateway.GatewayCache;
import org.dalipaj.apigateway.route.RouteEntity;
import org.dalipaj.apigateway.route.RouteRepository;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.dto.RouteTrie;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.response.RouteResponseRedisRepository;
import org.dalipaj.apigateway.upstream.backend.BackendDto;
import org.dalipaj.apigateway.upstream.service.ServiceDto;
import org.dalipaj.apigateway.upstream.service.ServiceEntity;
import org.dalipaj.apigateway.upstream.service.ServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

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
    private final IApplicationService applicationService;
    public static final String ENDPOINT = "/upstreams";

    @PostConstruct
    void initInMemoryRoutes() {
        gatewayCache.setRouteTrie(new RouteTrie());

        for (RouteEntity route : routeRepository.findAll()) {
            var routeDto = upstreamMapper.toRouteDto(route);
            gatewayCache.getRouteTrie().insert(routeDto);
            gatewayCache.addRouteUpstreams(routeDto);
        }
    }

    @Override
    public ServiceDto save(ServiceDto serviceDto, HttpServletRequest request) throws UnAuthorizedException {
        var entityId = upstreamTransactionalService.saveEntity(serviceDto, request);
        var entity = upstreamTransactionalService.findById(entityId);

        serviceDto = upstreamMapper.toServiceDto(entity);

        for (var route : serviceDto.getRoutes()) {
            gatewayCache.getRouteTrie().insert(route);
            gatewayCache.addRouteUpstreams(route);
        }

        return serviceDto;
    }

    @Override
    public RouteDto getRouteForRequest(String path) {
        return gatewayCache.getRouteTrie().match(path)
                .orElseThrow(() -> new NullPointerException("Route with path: " + path + " not found"));
    }

    @Override
    public List<BackendDto> getBackends(RouteDto routeDto) {
        return gatewayCache.getUpstreams(routeDto);
    }

    @Override
    public void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata,
                                         HttpMethod httpMethod) {
        var exactPath = routeRedisResponseWithMetadata.getExactPath();

        if (httpMethod == HttpMethod.GET) {
            log.info("Saving response for GET request on path: {} in redis", exactPath);
            routeResponseRedisRepository.save(routeRedisResponseWithMetadata);
        }
        else
            log.info("Not saving response for {} request on path: {} in redis, only GET responses are cached",
                    httpMethod, exactPath);
    }

    @Override
    public RouteRedisResponseWithMetadata getRouteResponseFromCache(String path, HttpMethod httpMethod) {
        if (HttpMethod.GET != httpMethod) {
            log.info("Not fetching response for {} request on path: {} from redis, only GET responses are cached",
                    httpMethod, path);
            return null;
        }

        return routeResponseRedisRepository.get(path);
    }

    @Override
    public void delete(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var entity = checkAppPermissionsAndGetEntity(id, request);
        serviceRepository.delete(entity);

        for (var route : entity.getRoutes()) {
            var deleted = gatewayCache.getRouteTrie().delete(route.getPath());
            log.info("Route {} was {} deleted from in memory", id, deleted ? "" : "not");
        }

    }

    @Override
    public ServiceDto getById(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var entity = checkAppPermissionsAndGetEntity(id, request);
        return upstreamMapper.toServiceDto(entity);
    }

    private ServiceEntity checkAppPermissionsAndGetEntity(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var entity = upstreamTransactionalService.findById(id);
        applicationService.checkAppPermissions(request, entity.getApplication().getName());

        return entity;
    }

    @Override
    public Page<ServiceDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters) {
        var entityPages = super.getAll(pageNumber, pageSize, filters, serviceRepository);

        return entityPages.map(upstreamMapper::toServiceDto);
    }
}
