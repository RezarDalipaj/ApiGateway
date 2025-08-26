package org.dalipaj.apigateway.rateLimit.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.FilterUtil;
import org.dalipaj.apigateway.gateway.GatewayCache;
import org.dalipaj.apigateway.rateLimit.RateLimitDto;
import org.dalipaj.apigateway.rateLimit.RateLimitEntity;
import org.dalipaj.apigateway.rateLimit.RateLimitException;
import org.dalipaj.apigateway.rateLimit.RateLimitMapper;
import org.dalipaj.apigateway.rateLimit.RateLimitProperties;
import org.dalipaj.apigateway.rateLimit.RateLimitRepository;
import org.dalipaj.apigateway.rateLimit.RateLimiter;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
import org.dalipaj.apigateway.user.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService implements IRateLimitService {

    private final RateLimitRepository rateLimitRepository;
    private final RateLimitMapper rateLimitMapper;
    private final RateLimiter rateLimiter;
    private final RateLimitProperties rateLimitProperties;
    private final GatewayCache gatewayCache;
    private final IUserService userService;

    public static final String API_KEY_HEADER = "x-api-key";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

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
    public RateLimitDto save(RateLimitDto rateLimitDto, HttpServletRequest request) throws UnAuthorizedException {
        String hashedApiKey;
        RateLimitEntity entity;
        var username = userService.getUsernameFromRequest(request);

        if (rateLimitDto.getId() == null) {
            entity = rateLimitMapper.toEntity(rateLimitDto);
            hashedApiKey = userService.encode(entity.getApiKey());
            entity.setApiKey(hashedApiKey);
            var user = userService.findUserByUsername(username);
            entity.setUser(user);
        } else {
            entity = updateDb(rateLimitDto, username);
            hashedApiKey = entity.getApiKey();
        }

        entity = rateLimitRepository.save(entity);
        rateLimitDto = rateLimitMapper.toDto(entity);
        
        gatewayCache.getRateLimits().put(hashedApiKey, rateLimitDto);
        return rateLimitDto;
    }
    
    private RateLimitEntity updateDb(RateLimitDto rateLimitDto, String usernameFromRequest) {
        var entity = validateUsernameThenGetEntity(rateLimitDto.getId(), usernameFromRequest);

        if (rateLimitDto.getPerMinute() != null)
            entity.setPerMinute(rateLimitDto.getPerMinute());

        if (rateLimitDto.getPerHour() != null)
            entity.setPerHour(rateLimitDto.getPerHour());

        if (rateLimitDto.getApiKey() != null)
            entity.setApiKey(userService.encode(rateLimitDto.getApiKey()));

        return entity;
    }

    private RateLimitEntity findById(Long id) {
        return rateLimitRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Rate limit with id: " + id + " not found"));
    }
    
    @Override
    public RateLimitDto getById(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var username = userService.getUsernameFromRequest(request);
        var entity = validateUsernameThenGetEntity(id, username);

        return rateLimitMapper.toDto(entity);
    }

    @Override
    public Page<RateLimitDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters) {
        var pageable = PageRequest.of(pageNumber, pageSize);

        FilterUtil<RateLimitEntity> routeFilterUtil = new FilterUtil<>();
        List<Specification<RateLimitEntity>> allSpecs = routeFilterUtil.getAllSpecs(filters);
        Specification<RateLimitEntity> specification = Specification.allOf(allSpecs);

        return rateLimitRepository.findAll(specification, pageable)
                .map(rateLimitMapper::toDto);
    }

    @Override
    public void delete(Long id, HttpServletRequest request) throws UnAuthorizedException {
        var username = userService.getUsernameFromRequest(request);
        var rateLimitEntity = validateUsernameThenGetEntity(id, username);

        rateLimitRepository.delete(rateLimitEntity);
        gatewayCache.getRateLimits().remove(rateLimitEntity.getApiKey());
    }

    private RateLimitDto getFromInMemory(String rawApiKey) {
        var hashedApiKey = userService.encode(rawApiKey);
        return gatewayCache.getRateLimits().get(hashedApiKey);
    }

    private RateLimitEntity validateUsernameThenGetEntity(Long id, String usernameFromRequest) {
        var entity = findById(id);
        userService.validateUsername(usernameFromRequest, entity.getUser().getUsername());

        return entity;
    }

    @Override
    public void allowRequest(HttpServletRequest request) throws RateLimitException {
        var apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null) {
            // IP-based
            rateLimiter.allowRequest("ip:" + getClientIp(request),
                    rateLimitProperties.getPerMinute(), rateLimitProperties.getPerHour());
        } else {
            var rateLimit = getFromInMemory(apiKey);
            rateLimiter.allowRequest("api:" + rateLimit.getApiKey(), rateLimit.getPerMinute(), rateLimit.getPerHour());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader(X_FORWARDED_FOR);
        if (xfHeader != null)
            return xfHeader.split(",")[0];

        return request.getRemoteAddr();
    }
}
