package org.dalipaj.apigateway.upstream.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.route.data.RouteDto;
import org.dalipaj.apigateway.route.data.RouteEntity;
import org.dalipaj.apigateway.route.data.RouteRepository;
import org.dalipaj.apigateway.upstream.UpstreamMapper;
import org.dalipaj.apigateway.upstream.data.backend.BackendDto;
import org.dalipaj.apigateway.upstream.data.backend.BackendEntity;
import org.dalipaj.apigateway.upstream.data.backend.BackendRepository;
import org.dalipaj.apigateway.upstream.data.service.ServiceDto;
import org.dalipaj.apigateway.upstream.data.service.ServiceEntity;
import org.dalipaj.apigateway.upstream.data.service.ServiceRepository;
import org.dalipaj.apigateway.upstream.service.IUpstreamTransactionalService;
import org.dalipaj.apigateway.user.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UpstreamTransactionalService implements IUpstreamTransactionalService {

    private final RouteRepository routeRepository;
    private final BackendRepository backendRepository;
    private final ServiceRepository serviceRepository;
    private final UpstreamMapper upstreamMapper;
    private final IUserService userService;

    @Transactional
    @Override
    public Long saveEntity(ServiceDto serviceDto,
                           HttpServletRequest request) throws UnAuthorizedException {
        if (serviceDto.getId() == null)
            return saveToDb(serviceDto, userService.getUsernameFromRequest(request));

        return updateDb(serviceDto, request);
    }

    @Override
    public ServiceEntity findById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Service with id: " + id + " not found"));
    }

    @Override
    public void deleteRouteFromBackends(Long serviceId) {
        backendRepository.deleteRouteBackendsByServiceId(serviceId);
    }

    private Long saveToDb(ServiceDto serviceDto, String appName) {
        var service = upstreamMapper.toServiceEntity(serviceDto);

        var app = userService.findByUsername(appName);
        service.setApplication(app);

        var savedService = serviceRepository.save(service);
        saveRoutes(savedService, serviceDto.getRoutes());

        return savedService.getId();
    }

    private Long updateDb(ServiceDto serviceDto,
                          HttpServletRequest request) throws UnAuthorizedException {
        var entity = findById(serviceDto.getId());
        userService.checkUserPermissions(request, entity.getApplication().getUsername());

        if (serviceDto.getName() != null)
            entity.setName(serviceDto.getName());

        var savedService = serviceRepository.save(entity);

        saveRoutes(savedService, serviceDto.getRoutes());

        return savedService.getId();
    }

    private void saveRoutes(ServiceEntity savedService,
                            List<RouteDto> routeDtos) {
        List<RouteEntity> routes = upstreamMapper.toRouteEntityList(savedService.getName(), routeDtos);

        for (int i = 0; i < routes.size(); i++) {
            var route = routes.get(i);

            if (route.getPath() == null)
                throw new NullPointerException("Route path cannot be null");

            var savedRoute = routeRepository.findByPath(route.getPath())
                    .map(existingRoute -> {
                        if (route.getLoadBalancerType() != null)
                            existingRoute.setLoadBalancerType(route.getLoadBalancerType());

                        return existingRoute;
                    }).orElse(route);

            savedRoute.setService(savedService);
            savedRoute = routeRepository.save(savedRoute);

            saveBackends(savedRoute, routeDtos.get(i).getBackends());
        }
    }

    private void saveBackends(RouteEntity savedRoute,
                              List<BackendDto> backendDtos) {
        var savedRouteId = savedRoute.getId();
        List<BackendEntity> backends = upstreamMapper.toBackendEntityList(backendDtos);

        for (var backend : backends) {
            if (backend.getHost() == null)
                throw new NullPointerException("Backend host cannot be null");

            var finalBackend = backendRepository.findByHost(backend.getHost()).map(existingBackend -> {
                if (backend.getHealthCheckPath() != null)
                    existingBackend.setHealthCheckPath(backend.getHealthCheckPath());

                if (backend.getWeight() != null)
                    existingBackend.setWeight(backend.getWeight());

                return existingBackend;
            }).orElse(backend);

            finalBackend = backendRepository.save(finalBackend);
            backendRepository.addRouteToBackendIfMissing(savedRouteId, finalBackend.getId());
        }
    }
}
