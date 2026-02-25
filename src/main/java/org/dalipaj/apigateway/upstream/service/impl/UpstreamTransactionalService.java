package org.dalipaj.apigateway.upstream.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.route.data.RouteDto;
import org.dalipaj.apigateway.route.data.RouteEntity;
import org.dalipaj.apigateway.route.data.RouteRepository;
import org.dalipaj.apigateway.upstream.UpstreamMapper;
import org.dalipaj.apigateway.upstream.data.target.TargetDto;
import org.dalipaj.apigateway.upstream.data.target.TargetEntity;
import org.dalipaj.apigateway.upstream.data.target.TargetRepository;
import org.dalipaj.apigateway.upstream.data.service.ServiceDto;
import org.dalipaj.apigateway.upstream.data.service.ServiceEntity;
import org.dalipaj.apigateway.upstream.data.service.ServiceRepository;
import org.dalipaj.apigateway.upstream.service.IUpstreamTransactionalService;
import org.dalipaj.apigateway.user.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;


@Slf4j
@Service
@RequiredArgsConstructor
public class UpstreamTransactionalService implements IUpstreamTransactionalService {

    private final RouteRepository routeRepository;
    private final TargetRepository targetRepository;
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
    public void deleteRouteFromTargets(Long serviceId) {
        targetRepository.deleteTargetRoutesByServiceId(serviceId);
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
        if (isEmpty(routes)) {
            log.info("No routes to save for service with id: {}", savedService.getId());
            return;
        }

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

            saveTargets(savedRoute, routeDtos.get(i).getTargets());
        }
    }

    private void saveTargets(RouteEntity savedRoute,
                             List<TargetDto> targetDtos) {
        var savedRouteId = savedRoute.getId();
        List<TargetEntity> targets = upstreamMapper.toTargetEntityList(targetDtos);

        if (isEmpty(targets)) {
            log.info("No targets to save for route with id: {}", savedRouteId);
            return;
        }

        for (var target : targets) {
            if (target.getHost() == null)
                throw new NullPointerException("Target host cannot be null");

            var finalTarget = targetRepository.findByHost(target.getHost()).map(existingTarget -> {
                if (target.getHealthCheckPath() != null)
                    existingTarget.setHealthCheckPath(target.getHealthCheckPath());

                if (target.getWeight() != null)
                    existingTarget.setWeight(target.getWeight());

                return existingTarget;
            }).orElse(target);

            finalTarget = targetRepository.save(finalTarget);
            targetRepository.addRouteToTargetIfMissing(savedRouteId, finalTarget.getId());
        }
    }
}
