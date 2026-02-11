package org.dalipaj.apigateway.route.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.FilterUtil;
import org.dalipaj.apigateway.gateway.GatewayCache;
import org.dalipaj.apigateway.route.RouteEntity;
import org.dalipaj.apigateway.route.RouteMapper;
import org.dalipaj.apigateway.route.RouteRepository;
import org.dalipaj.apigateway.route.backend.BackendEntity;
import org.dalipaj.apigateway.route.backend.BackendRepository;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.dto.RouteTrie;
import org.dalipaj.apigateway.route.oauth.OAuthEntity;
import org.dalipaj.apigateway.route.oauth.OAuthRepository;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.route.response.RouteResponseRedisRepository;
import org.dalipaj.apigateway.route.service.IRouteService;
import org.dalipaj.apigateway.user.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService implements IRouteService {

    private final RouteRepository routeRepository;
    private final BackendRepository backendRepository;
    private final OAuthRepository oAuthRepository;
    private final RouteMapper routeMapper;
    private final GatewayCache gatewayCache;
    private final RouteResponseRedisRepository routeResponseRedisRepository;
    private final IUserService userService;

    @PostConstruct
    void initInMemoryRoutes() {
        gatewayCache.setRouteTrie(new RouteTrie());
        var allRoutes = routeRepository.findAll();

        for (RouteEntity route : allRoutes) {
            var routeDto = routeMapper.toDto(route);
            gatewayCache.getRouteTrie().insert(routeDto);
        }
    }

    @Override
    public RouteDto save(RouteDto routeDto, HttpServletRequest request) throws UnAuthorizedException {
        RouteEntity entity = routeRepository.findById(routeDto.getPath()).orElse(null);
        var username = userService.getUsernameFromRequest(request);

        if (entity == null) {
            entity = saveToDb(routeDto, username);
        } else {
            updateDb(entity, routeDto, username);
        }

        entity = routeRepository.save(entity);
        routeDto = routeMapper.toDto(entity);

        gatewayCache.getRouteTrie().insert(routeDto);

        return routeDto;
    }

    private void updateDb(RouteEntity entity, RouteDto routeDto, String username) {
        userService.validateUsername(username, entity.getUser().getUsername());

        if (!isEmpty(routeDto.getBackends())) {
            List<BackendEntity> backendEntities = routeMapper.toBackends(routeDto.getBackends());
            entity.setBackends(saveOrGetBackends(backendEntities));
        }

        if (routeDto.getStripPrefix() != null)
            entity.setStripPrefix(routeDto.getStripPrefix());

        if (routeDto.getOauth() != null) {
            var oauthEntity = routeMapper.toOAuth(routeDto.getOauth());
            entity.setOauth(saveOrGetOAuth(oauthEntity));
        }

        if (routeDto.getAuthType() != null)
            entity.setAuthType(routeDto.getAuthType());

        if (routeDto.getLoadBalancerType() != null)
            entity.setLoadBalancerType(routeDto.getLoadBalancerType());
    }

    private RouteEntity saveToDb(RouteDto routeDto, String username) {
        var route = routeMapper.toEntity(routeDto);

        route.setBackends(saveOrGetBackends(route.getBackends()));
        route.setOauth(saveOrGetOAuth(route.getOauth()));
        var user = userService.findUserByUsername(username);
        route.setUser(user);

        return route;
    }

    private List<BackendEntity> saveOrGetBackends(List<BackendEntity> backends) {
        return backends.stream()
                .map(backend -> {
                    var entity = backendRepository.findById(backend.getUrl());
                    return entity.orElseGet(() -> backendRepository.save(backend));
                }).toList();
    }

    private OAuthEntity saveOrGetOAuth(OAuthEntity oauth) {
        if (oauth == null)
            return null;

        if (oauth.getId() != null) {
            return oAuthRepository.findById(oauth.getId())
                    .orElseThrow(() -> new NullPointerException("OAuth with id " + oauth.getId() + " not found"));
        }

        return oAuthRepository.save(oauth);
    }

    @Override
    public RouteDto getRouteForRequest(String path) {
        return gatewayCache.getRouteTrie().match(path)
                .orElseThrow(() -> new NullPointerException("Route with path: " + path + " not found"));
    }

    @Override
    public void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata) {
        routeResponseRedisRepository.save(routeRedisResponseWithMetadata);
    }

    @Override
    public RouteRedisResponseWithMetadata getRouteResponseFromCache(String path) {
        return routeResponseRedisRepository.get(path);
    }

    private RouteEntity findByPath(String path) {
        return routeRepository.findById(path)
                .orElseThrow(() -> new NullPointerException("Route: " + path + " not found"));
    }

    @Override
    public void delete(String path, HttpServletRequest request) throws UnAuthorizedException {
        var entity = validateUsernameAndGetEntity(path, request);
        routeRepository.delete(entity);

        var deleted = gatewayCache.getRouteTrie().delete(path);
        log.info("Route {} was {} deleted from in memory", path, deleted ? "" : "not");
    }

    @Override
    public RouteDto getByPath(String path, HttpServletRequest request) throws UnAuthorizedException {
        var entity = validateUsernameAndGetEntity(path, request);
        return routeMapper.toDto(entity);
    }

    private RouteEntity validateUsernameAndGetEntity(String path, HttpServletRequest request) throws UnAuthorizedException {
        var entity = findByPath(path);

        var usernameFromRequest = userService.getUsernameFromRequest(request);
        userService.validateUsername(usernameFromRequest, entity.getUser().getUsername());

        return entity;
    }

    @Override
    public Page<RouteDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters) {
        var pageable = PageRequest.of(pageNumber, pageSize);

        FilterUtil<RouteEntity> routeFilterUtil = new FilterUtil<>();
        List<Specification<RouteEntity>> allSpecs = routeFilterUtil.getAllSpecs(filters);
        Specification<RouteEntity> specification = Specification.allOf(allSpecs);

        return routeRepository.findAll(specification, pageable)
                .map(routeMapper::toDto);
    }
}
