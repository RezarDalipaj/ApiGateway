package org.dalipaj.apigateway.rateLimit.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.rateLimit.RateLimitDto;
import org.dalipaj.apigateway.rateLimit.RateLimitException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRateLimitService {

    @Transactional
    RateLimitDto save(RateLimitDto rateLimitDto, HttpServletRequest request) throws UnAuthorizedException;

    RateLimitDto getById(Long id, HttpServletRequest request) throws UnAuthorizedException;

    Page<RateLimitDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters);

    @Transactional
    void delete(Long id, HttpServletRequest request) throws UnAuthorizedException;

    void allowRequest(HttpServletRequest request) throws RateLimitException;
}
