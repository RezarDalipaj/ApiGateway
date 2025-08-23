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
        var hashedApiKey = hashService.encode(rateLimitDto.getApiKey());
        RateLimitEntity entity = rateLimitRepository.findByApiKey(hashedApiKey)
                .orElse(null);

        if (entity == null) {
            entity = rateLimitMapper.toEntity(rateLimitDto);
            entity.setApiKey(hashedApiKey);
        } else {
            updateDb(entity, rateLimitDto);
        }

        entity = rateLimitRepository.save(entity);
        rateLimitDto = rateLimitMapper.toDto(entity);
        
        gatewayCache.getRateLimits().put(hashedApiKey, rateLimitDto);
        return rateLimitDto;
    }
    
    private void updateDb(RateLimitEntity entity, RateLimitDto rateLimitDto) {
        if (rateLimitDto.getPerMinute() != null)
            entity.setPerMinute(rateLimitDto.getPerMinute());

        if (rateLimitDto.getPerHour() != null)
            entity.setPerHour(rateLimitDto.getPerHour());
    }
    
    @Override
    public RateLimitDto getById(Long id) {
        return rateLimitMapper.toDto(
                rateLimitRepository.findById(id)
                        .orElseThrow(() -> new NullPointerException("Rate limit with id: " + id + " not found")));
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
        var rateLimitEntity = rateLimitRepository.findById(id)
                .orElse(null);

        if (rateLimitEntity == null) {
            log.info("Rate limit with id {} not found", id);
            return;
        }

        rateLimitRepository.delete(rateLimitEntity);
        gatewayCache.getRateLimits().remove(rateLimitEntity.getApiKey());
    }
}
