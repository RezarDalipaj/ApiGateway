package org.dalipaj.apigateway.rateLimit.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.filter.FilterDto;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.rateLimit.data.RateLimitDto;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Validated
public interface IRateLimitService {

    void allowRequest(HttpServletRequest request, String clientIp) throws RateLimitException, NoSuchAlgorithmException;

    @Transactional
    RateLimitDto save(RateLimitDto rateLimitDto, HttpServletRequest request) throws UnAuthorizedException,
            RateLimitException, BadRequestException, NoSuchAlgorithmException;

    RateLimitDto getById(Long id, HttpServletRequest request) throws UnAuthorizedException;

    Page<RateLimitDto> getAll(Integer pageNumber, Integer pageSize, List<FilterDto> filters);

    @Transactional
    void delete(Long id, HttpServletRequest request) throws UnAuthorizedException;
}
