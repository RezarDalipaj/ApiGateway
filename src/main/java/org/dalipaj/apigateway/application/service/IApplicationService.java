package org.dalipaj.apigateway.application.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.application.ApplicationDto;
import org.dalipaj.apigateway.application.ApplicationEntity;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.rateLimit.RateLimitException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Validated
public interface IApplicationService {

    ApplicationEntity findByName(String appName);

    Page<ApplicationDto> findAll(int pageNumber, List<FilterDto> filters, int size);

    @Transactional
    ApplicationDto save(ApplicationDto app, HttpServletRequest request) throws BadRequestException, UnAuthorizedException;

    ApplicationDto getById(long id, HttpServletRequest request) throws UnAuthorizedException;

    @Transactional
    void delete(long id, HttpServletRequest request) throws BadRequestException, UnAuthorizedException;

    ApplicationDto getByName(String appName);

    String getAppNameFromRequest(HttpServletRequest request) throws UnAuthorizedException;

    void checkAppPermissions(HttpServletRequest request, String actualAppName) throws UnAuthorizedException;

    String hash(String raw);

    String sha256(String raw) throws RateLimitException, NoSuchAlgorithmException;
}
