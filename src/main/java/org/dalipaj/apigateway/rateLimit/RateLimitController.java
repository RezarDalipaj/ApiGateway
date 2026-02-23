package org.dalipaj.apigateway.rateLimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(RateLimitProperties.ENDPOINT)
@RequiredArgsConstructor
public class RateLimitController {

    private final IRateLimitService rateLimitService;

    @PostMapping
    public ResponseEntity<RateLimitDto> create(@Validated(OnCreateGroup.class)
                                               @RequestBody RateLimitDto rateLimitDto,
                                               HttpServletRequest request)
            throws UnAuthorizedException, RateLimitException, BadRequestException, NoSuchAlgorithmException {

        var createdRateLimit = rateLimitService.save(rateLimitDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRateLimit);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<RateLimitDto>> getAll(@RequestBody(required = false)
                                                     @Valid List<FilterDto> filters,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        var filteredRateLimits = rateLimitService.getAll(page, size, filters);
        return ResponseEntity.ok(filteredRateLimits);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RateLimitDto> delete(@PathVariable Long id,
                                               HttpServletRequest request) throws UnAuthorizedException {
        rateLimitService.delete(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RateLimitDto> get(@PathVariable Long id,
                                            HttpServletRequest request) throws UnAuthorizedException {
        var rateLimit = rateLimitService.getById(id, request);
        return ResponseEntity.ok(rateLimit);
    }
}

