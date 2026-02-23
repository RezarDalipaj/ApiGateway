package org.dalipaj.apigateway.application.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.application.ApplicationDto;
import org.dalipaj.apigateway.application.ApplicationEntity;
import org.dalipaj.apigateway.application.ApplicationRepository;
import org.dalipaj.apigateway.application.mapper.ApplicationMapper;
import org.dalipaj.apigateway.application.service.IApplicationService;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.dalipaj.apigateway.auth.service.impl.TokenProvider;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.PaginationService;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService extends PaginationService implements IApplicationService {

    private final ApplicationRepository applicationRepository;
    private final IHashService hashService;
    private final ApplicationMapper applicationMapper;
    private final TokenProvider tokenProvider;
    public static final String ENDPOINT = "/applications";

    @Override
    public ApplicationEntity findByName(String appName) {
        var entity = applicationRepository.findByName(appName);
        if (entity == null)
            throw new NullPointerException("Application with name " + appName + " does not exist");
        return entity;
    }

    @Override
    public Page<ApplicationDto> findAll(int pageNumber, List<FilterDto> filters, int size) {
        var entityPages = super.getAll(pageNumber, size, filters, applicationRepository);
        return entityPages.map(applicationMapper::toAppDto);
    }

    @Override
    @Transactional
    public ApplicationDto save(ApplicationDto applicationDto,
                               HttpServletRequest request) throws BadRequestException, UnAuthorizedException {
        if (applicationDto.getId() == null)
            return create(applicationDto);

        return update(applicationDto, request);
    }

    @Override
    public ApplicationDto getById(long id, HttpServletRequest request) throws UnAuthorizedException {
        var entity = findEntityById(id);
        checkAppPermissions(request, entity.getName());
        return applicationMapper.toAppDto(entity);
    }

    @Override
    @Transactional
    public void delete(long id, HttpServletRequest request) throws BadRequestException, UnAuthorizedException {
        var appEntity = findEntityById(id);
        checkAppPermissions(request, appEntity.getName());

        applicationRepository.deleteById(id);
    }

    @Override
    public ApplicationDto getByName(String appName) {
        var application = findByName(appName);
        return applicationMapper.toAppDto(application);
    }

    @Override
    public String getAppNameFromRequest(HttpServletRequest request) throws UnAuthorizedException {
        return tokenProvider.getAppNameFromRequest(request);
    }

    @Override
    public void checkAppPermissions(HttpServletRequest request, String actualAppName) throws UnAuthorizedException {
        if (!getAppNameFromRequest(request).equals(actualAppName))
            throw new AccessDeniedException("Access denied: Cannot access this resource");
    }

    @Override
    public String hash(String raw) {
        return hashService.hash(raw);
    }

    @Override
    public String sha256(String raw) throws NoSuchAlgorithmException {
        return hashService.sha256(raw);
    }

    private ApplicationDto create(ApplicationDto applicationDto) throws BadRequestException {
        validateAppNameIsUnique(applicationDto.getName());

        var appEntity = applicationMapper.toAppEntity(applicationDto);
        setPassword(appEntity, applicationDto.getPassword());
        var entity = applicationRepository.save(appEntity);
        return applicationMapper.toAppDto(entity);
    }

    private ApplicationDto update(ApplicationDto applicationDto,
                                  HttpServletRequest request) throws BadRequestException, UnAuthorizedException {
        var appEntity = findEntityById(applicationDto.getId());
        checkAppPermissions(request, appEntity.getName());

        var appName = applicationDto.getName();
        var rawPassword = applicationDto.getPassword();

        if (Strings.isNotBlank(appName)) {
            validateAppNameIsUnique(appName);
            appEntity.setName(appName);
        }

        if (Strings.isNotBlank(rawPassword))
            setPassword(appEntity, rawPassword);

        var entity = applicationRepository.save(appEntity);
        return applicationMapper.toAppDto(entity);
    }

    private ApplicationEntity findEntityById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Application with id ".concat(id.toString()).concat(" not found")));
    }

    private void setPassword(ApplicationEntity application, String rawPassword) {
        application.setPassword(hash(rawPassword));
    }

    private void validateAppNameIsUnique(String application) throws BadRequestException {
        if (applicationRepository.existsByName(application))
            throw new BadRequestException("Application already exists");
    }
}
