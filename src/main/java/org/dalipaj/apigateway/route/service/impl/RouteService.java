package org.dalipaj.apigateway.route.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.route.RouteMapper;
import org.dalipaj.apigateway.route.service.IRouteService;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.route.response.RouteRedisResponseWithMetadata;
import org.dalipaj.apigateway.gateway.GatewayCache;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.dto.RouteTree;
import org.dalipaj.apigateway.route.backend.BackendEntity;
import org.dalipaj.apigateway.route.oauth.OAuthEntity;
import org.dalipaj.apigateway.route.RouteEntity;
import org.dalipaj.apigateway.route.backend.BackendRepository;
import org.dalipaj.apigateway.route.oauth.OAuthRepository;
import org.dalipaj.apigateway.route.RouteRepository;
import org.dalipaj.apigateway.route.response.RouteResponseRedisRepository;
import org.dalipaj.apigateway.route.RouteUtil;
import org.dalipaj.apigateway.common.FilterUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    @PostConstruct
    void initInMemoryRoutes() {
        gatewayCache.setRouteTrees(new HashMap<>());
        var allRoutes = routeRepository.findAll();

        for (RouteEntity route : allRoutes) {
            var routeDto = routeMapper.toDto(route);

            var mainPath = RouteUtil.getMainPath(routeDto.getPath());
            RouteTree routeTree = new RouteTree();
            routeTree.save(routeDto);

            gatewayCache.getRouteTrees().put(mainPath, routeTree);
        }
    }

    @Override
    public RouteDto save(RouteDto routeDto) {
        RouteEntity entity = routeRepository.findById(routeDto.getPath())
                .orElse(null);

        if (entity == null) {
            entity = saveToDb(routeDto);
        } else {
            updateDb(entity, routeDto);
        }

        entity = routeRepository.save(entity);
        routeDto = routeMapper.toDto(entity);

        var mainPath = RouteUtil.getMainPath(entity.getPath());
        var routeTree = gatewayCache.getRouteTrees().get(mainPath);

        if (routeTree == null) {
            routeTree = new RouteTree();
            routeTree.save(routeDto);
            gatewayCache.getRouteTrees().put(mainPath, routeTree);
        } else {
            routeTree.save(routeDto);
        }

        return routeDto;
    }

    private void updateDb(RouteEntity entity, RouteDto routeDto) {
        if (!isEmpty(routeDto.getBackends())) {
            List<BackendEntity> backendEntities = routeMapper.toBackends(routeDto.getBackends());
            entity.setBackends(saveOrGetBackends(backendEntities));
        }

        if (routeDto.getStripPrefix() != null) {
            entity.setStripPrefix(routeDto.getStripPrefix());
        }

        if (routeDto.getOauth() != null) {
            var oauthEntity = routeMapper.toOAuth(routeDto.getOauth());
            entity.setOauth(saveOrGetOAuth(oauthEntity));
        }

        if (routeDto.getAuthType() != null) {
            entity.setAuthType(routeDto.getAuthType());
        }
    }

    private RouteEntity saveToDb(RouteDto routeDto) {
        var route = routeMapper.toEntity(routeDto);

        route.setBackends(saveOrGetBackends(route.getBackends()));
        route.setOauth(saveOrGetOAuth(route.getOauth()));

        return route;
    }

    private List<BackendEntity> saveOrGetBackends(List<BackendEntity> backends) {
        return backends.stream()
                .map(backend -> {
                    if (!backendRepository.existsById(backend.getUrl()))
                        return backendRepository.save(backend);

                    return backend;
                }).toList();
    }

    private OAuthEntity saveOrGetOAuth(OAuthEntity oauth) {
        if (oauth == null)
            return null;

        if (oauth.getId() != null) {
            return oAuthRepository.findById(oauth.getId())
                    .orElse(null);
        }

        return oAuthRepository.save(oauth);
    }

    @Override
    public RouteDto getRouteForRequest(String path, String method) {
        var mainPath = RouteUtil.getMainPath(path);
        var routeTree = gatewayCache.getRouteTrees().get(mainPath);
        if (routeTree == null)
            throw new NullPointerException("Main route: " + mainPath + " not found");

        return routeTree.find(path);
    }

    @Override
    public void saveRouteResponseInCache(RouteRedisResponseWithMetadata routeRedisResponseWithMetadata) {
        routeResponseRedisRepository.saveResponse(routeRedisResponseWithMetadata);
    }

    @Override
    public RouteRedisResponseWithMetadata getRouteResponseFromCache(String path) {
        return routeResponseRedisRepository.getResponse(path);
    }

    @Override
    public void delete(String path) {
        routeRepository.deleteById(path);

        var routeTree = gatewayCache.getRouteTree(path);
        if (routeTree != null) {
            var deleted = routeTree.delete(path);
            log.info("Route {} was {} deleted from in memory", path, deleted ? "" : "not");
        }
    }

    @Override
    public RouteDto getByPath(String path) {
        return routeMapper.toDto(
                routeRepository.findById(path)
                        .orElseThrow(() -> new NullPointerException("Route: " + path + " not found")));
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
