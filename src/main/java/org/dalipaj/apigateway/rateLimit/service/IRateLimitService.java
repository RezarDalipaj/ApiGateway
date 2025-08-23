package org.dalipaj.apigateway.rateLimit.service;

import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.rateLimit.RateLimitDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRateLimitService {

    @Transactional
    RateLimitDto save(RateLimitDto rateLimitDto);

    RateLimitDto getById(Long id);

    Page<RateLimitDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters);

    @Transactional
    void delete(Long id);
}
