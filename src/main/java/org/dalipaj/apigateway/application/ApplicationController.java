package org.dalipaj.apigateway.application;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.application.service.impl.ApplicationService;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.application.service.IApplicationService;
import org.dalipaj.apigateway.common.validation.OnUpdateGroup;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApplicationService.ENDPOINT)
@RequiredArgsConstructor
public class ApplicationController {

    private final IApplicationService applicationService;

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDto> getById(@PathVariable("id") long id,
                                                 HttpServletRequest request) throws UnAuthorizedException {
        return ResponseEntity.ok(applicationService.getById(id, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationDto> update(@Validated(OnUpdateGroup.class)
                                                 @RequestBody ApplicationDto applicationDto,
                                                 @PathVariable("id") long id,
                                                 HttpServletRequest request) throws BadRequestException, UnAuthorizedException {
        applicationDto.setId(id);
        return ResponseEntity.ok(applicationService.save(applicationDto, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id,
                                       HttpServletRequest request) throws BadRequestException, UnAuthorizedException {
        applicationService.delete(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<ApplicationDto>> getAll(@RequestBody(required = false)
                                                       @Valid List<FilterDto> filters,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.findAll(page, filters, size));
    }
}
