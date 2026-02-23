package org.dalipaj.apigateway.upstream;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.application.service.IApplicationService;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.route.RouteEntity;
import org.dalipaj.apigateway.route.RouteRepository;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.route.oauth.OAuthEntity;
import org.dalipaj.apigateway.route.oauth.OAuthRepository;
import org.dalipaj.apigateway.upstream.backend.BackendDto;
import org.dalipaj.apigateway.upstream.backend.BackendEntity;
import org.dalipaj.apigateway.upstream.backend.BackendRepository;
import org.dalipaj.apigateway.upstream.service.ServiceDto;
import org.dalipaj.apigateway.upstream.service.ServiceEntity;
import org.dalipaj.apigateway.upstream.service.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UpstreamTransactionalService implements IUpstreamTransactionalService {

    private final RouteRepository routeRepository;
    private final BackendRepository backendRepository;
    private final ServiceRepository serviceRepository;
    private final OAuthRepository oAuthRepository;
    private final UpstreamMapper upstreamMapper;
    private final IApplicationService applicationService;

    @Transactional
    @Override
    public Long saveEntity(ServiceDto serviceDto,
                           HttpServletRequest request) throws UnAuthorizedException {
        if (serviceDto.getId() == null)
            return saveToDb(serviceDto, applicationService.getAppNameFromRequest(request));

        return updateDb(serviceDto, request);
    }

    @Override
    public ServiceEntity findById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Service with id: " + id + " not found"));
    }

    private Long saveToDb(ServiceDto serviceDto, String appName) {
        var service = upstreamMapper.toServiceEntity(serviceDto);

        var app = applicationService.findByName(appName);
        service.setApplication(app);

        var savedService = serviceRepository.save(service);
        saveRoutes(savedService, serviceDto.getRoutes());

        return savedService.getId();
    }

    private Long updateDb(ServiceDto serviceDto,
                          HttpServletRequest request) throws UnAuthorizedException {
        var entity = findById(serviceDto.getId());
        applicationService.checkAppPermissions(request, entity.getApplication().getName());

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
                        if (route.getAuthType() != null)
                            existingRoute.setAuthType(route.getAuthType());

                        if (route.getLoadBalancerType() != null)
                            existingRoute.setLoadBalancerType(route.getLoadBalancerType());

                        if (route.getOauth() != null)
                            existingRoute.setOauth(saveOrGetOAuth(route.getOauth()));

                        return existingRoute;
                    }).orElseGet(() -> {
                        if (route.getOauth() != null)
                            route.setOauth(saveOrGetOAuth(route.getOauth()));

                        return route;
                    });

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

    private OAuthEntity saveOrGetOAuth(OAuthEntity oauth) {
        if (oauth.getName() == null)
            throw new NullPointerException("OAuth name cannot be null");

        var optionalExistingOAuth = oAuthRepository.findByName(oauth.getName());
        return optionalExistingOAuth.map(existingOAuth -> {
            if (oauth.getTokenEndpoint() != null)
                existingOAuth.setTokenEndpoint(oauth.getTokenEndpoint());

            if (oauth.getClientId() != null)
                existingOAuth.setClientId(oauth.getClientId());

            if (oauth.getClientSecret() != null)
                existingOAuth.setClientSecret(oauth.getClientSecret());

            if (oauth.getScope() != null)
                existingOAuth.setScope(oauth.getScope());

            return oAuthRepository.save(existingOAuth);
        }).orElseGet(() -> oAuthRepository.save(oauth));
    }
}
