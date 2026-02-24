package org.dalipaj.apigateway.rateLimit.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.application.IApplicationService;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.filter.FilterDto;
import org.dalipaj.apigateway.common.pagination.PaginationService;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.gateway.localcache.GatewayCache;
import org.dalipaj.apigateway.rateLimit.data.ApiKeyEntity;
import org.dalipaj.apigateway.rateLimit.data.ApiKeyRepository;
import org.dalipaj.apigateway.rateLimit.data.RateLimitDto;
import org.dalipaj.apigateway.rateLimit.data.RateLimitEntity;
import org.dalipaj.apigateway.rateLimit.service.RateLimitException;
import org.dalipaj.apigateway.rateLimit.service.RateLimitMapper;
import org.dalipaj.apigateway.rateLimit.service.RateLimitProperties;
import org.dalipaj.apigateway.rateLimit.data.RateLimitRepository;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService extends PaginationService implements IRateLimitService {

    private final RateLimitRepository rateLimitRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final RateLimitMapper rateLimitMapper;
    private final RateLimiter rateLimiter;
    private final RateLimitProperties rateLimitProperties;
    private final GatewayCache gatewayCache;
    private final IApplicationService applicationService;

    public static final String API_KEY_HEADER = "x-api-key";

    @PostConstruct
    void initInMemoryRateLimits() {
        gatewayCache.setRateLimits(new HashMap<>());
        List<RateLimitEntity> rateLimits = rateLimitRepository.findAll();

        for (RateLimitEntity rateLimit : rateLimits) {
            var rateLimitDto = rateLimitMapper.toDto(rateLimit);
            gatewayCache.getRateLimits().put(rateLimitDto.getApiKey(), rateLimitDto);
        }
    }

    @Override
    public void allowRequest(HttpServletRequest request,
                             String clientIp)
            throws RateLimitException, NoSuchAlgorithmException {

        var apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null) {
            // IP-based
            rateLimiter.allowRequest("ip:" + clientIp,
                    rateLimitProperties.getPerMinute(), rateLimitProperties.getPerHour());
        } else {
            var rateLimit = getFromInMemory(apiKey);
            rateLimiter.allowRequest("api:" + rateLimit.getApiKey(), rateLimit.getPerMinute(), rateLimit.getPerHour());
        }
    }

    @Override
    public RateLimitDto save(RateLimitDto rateLimitDto,
                             HttpServletRequest request)
            throws UnAuthorizedException, RateLimitException, BadRequestException, NoSuchAlgorithmException {

        var rawApiKey = rateLimitDto.getApiKey();
        String sha256LookupKey = applicationService.sha256(rawApiKey);
        apiKeyIsUnique(sha256LookupKey);

        var entity = rateLimitMapper.toEntity(rateLimitDto);
        var appName = applicationService.getAppNameFromRequest(request);
        var application = applicationService.findByName(appName);
        entity.setApplication(application);

        var apiKeyEntity = apiKeyRepository.save(ApiKeyEntity.builder()
                .lookupKey(sha256LookupKey)
                .secretHash(applicationService.hash(rawApiKey))
                .build());

        entity.setApiKey(apiKeyEntity);
        entity = rateLimitRepository.save(entity);
        rateLimitDto = rateLimitMapper.toDto(entity);
        
        gatewayCache.getRateLimits().put(sha256LookupKey, rateLimitDto);
        return rateLimitDto;
    }
    
    @Override
    public RateLimitDto getById(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var entity = checkAppPermissionsThenGetEntity(id, request);

        return rateLimitMapper.toDto(entity);
    }

    @Override
    public Page<RateLimitDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters) {
        var entityPages = super.getAll(pageNumber, pageSize, filters, rateLimitRepository);

        return entityPages.map(rateLimitMapper::toDto);
    }

    @Override
    public void delete(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var rateLimitEntity = checkAppPermissionsThenGetEntity(id, request);

        rateLimitRepository.delete(rateLimitEntity);
        gatewayCache.getRateLimits().remove(rateLimitEntity.getApiKey().getLookupKey());
    }

    private void apiKeyIsUnique(String sha256LookupKey) throws BadRequestException {
        if (sha256LookupKey != null
                && gatewayCache.getRateLimits().containsKey(sha256LookupKey))
            throw new BadRequestException("API key must be unique");
    }

    private RateLimitEntity findById(Long id) {
        return rateLimitRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Rate limit with id: " + id + " not found"));
    }

    private RateLimitDto getFromInMemory(String rawApiKey)
            throws RateLimitException, NoSuchAlgorithmException {

        var hashedApiKey = applicationService.sha256(rawApiKey);
        return gatewayCache.getRateLimits().get(hashedApiKey);
    }

    private RateLimitEntity checkAppPermissionsThenGetEntity(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var entity = findById(id);
        applicationService.checkAppPermissions(request, entity.getApplication().getName());

        return entity;
    }
}
