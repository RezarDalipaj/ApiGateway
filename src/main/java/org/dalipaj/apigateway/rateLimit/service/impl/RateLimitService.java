package org.dalipaj.apigateway.rateLimit.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.FilterUtil;
import org.dalipaj.apigateway.gateway.GatewayCache;
import org.dalipaj.apigateway.rateLimit.RateLimitDto;
import org.dalipaj.apigateway.rateLimit.RateLimitEntity;
import org.dalipaj.apigateway.rateLimit.RateLimitMapper;
import org.dalipaj.apigateway.rateLimit.RateLimitRepository;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
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
    private final GatewayCache gatewayCache;
    private final IHashService hashService;

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
    public RateLimitDto save(RateLimitDto rateLimitDto) {
        String hashedApiKey;
        RateLimitEntity entity;

        if (rateLimitDto.getId() == null) {
            entity = rateLimitMapper.toEntity(rateLimitDto);
            hashedApiKey = hashService.encode(entity.getApiKey());
            entity.setApiKey(hashedApiKey);
        } else {
            entity = updateDb(rateLimitDto);
            hashedApiKey = entity.getApiKey();
        }

        entity = rateLimitRepository.save(entity);
        rateLimitDto = rateLimitMapper.toDto(entity);
        
        gatewayCache.getRateLimits().put(hashedApiKey, rateLimitDto);
        return rateLimitDto;
    }
    
    private RateLimitEntity updateDb(RateLimitDto rateLimitDto) {
        var entity = findById(rateLimitDto.getId());

        if (rateLimitDto.getPerMinute() != null)
            entity.setPerMinute(rateLimitDto.getPerMinute());

        if (rateLimitDto.getPerHour() != null)
            entity.setPerHour(rateLimitDto.getPerHour());

        if (rateLimitDto.getApiKey() != null)
            entity.setApiKey(hashService.encode(rateLimitDto.getApiKey()));

        return entity;
    }

    private RateLimitEntity findById(Long id) {
        return rateLimitRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Rate limit with id: " + id + " not found"));
    }
    
    @Override
    public RateLimitDto getById(Long id) {
        return rateLimitMapper.toDto(findById(id));
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
    public void delete(Long id) {
        var rateLimitEntity = findById(id);

        rateLimitRepository.delete(rateLimitEntity);
        gatewayCache.getRateLimits().remove(rateLimitEntity.getApiKey());
    }

    @Override
    public RateLimitDto getFromInMemory(String rawApiKey) {
        var hashedApiKey = hashService.encode(rawApiKey);
        return gatewayCache.getRateLimits().get(hashedApiKey);
    }
}
