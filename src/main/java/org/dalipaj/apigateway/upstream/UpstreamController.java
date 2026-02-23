package org.dalipaj.apigateway.upstream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.common.validation.OnUpdateGroup;
import org.dalipaj.apigateway.upstream.service.ServiceDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UpstreamService.ENDPOINT)
@RequiredArgsConstructor
public class UpstreamController {

    private final IUpstreamService upstreamService;

    @PostMapping
    public ResponseEntity<ServiceDto> create(@Validated(OnCreateGroup.class)
                                           @RequestBody ServiceDto serviceDto,
                                             HttpServletRequest request) throws UnAuthorizedException {
        var createdService = upstreamService.save(serviceDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<ServiceDto>> getAll(@RequestBody(required = false)
                                                 @Valid List<FilterDto> filters,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        var filteredServices = upstreamService.getAll(page, size, filters);
        return ResponseEntity.ok(filteredServices);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> update(@Validated(OnUpdateGroup.class)
                                           @RequestBody ServiceDto serviceDto,
                                           @PathVariable Long id,
                                           HttpServletRequest request) throws UnAuthorizedException {
        serviceDto.setId(id);
        var updatedService = upstreamService.save(serviceDto, request);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                           HttpServletRequest request) throws UnAuthorizedException {
        upstreamService.delete(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> get(@PathVariable Long id,
                                        HttpServletRequest request) throws UnAuthorizedException {
        var service = upstreamService.getById(id, request);
        return ResponseEntity.ok(service);
    }
}
