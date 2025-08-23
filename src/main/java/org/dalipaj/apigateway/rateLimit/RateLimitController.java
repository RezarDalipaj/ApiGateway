package org.dalipaj.apigateway.rateLimit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.rateLimit.service.IRateLimitService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/rate-limits")
@RequiredArgsConstructor
public class RateLimitController {

    private final IRateLimitService rateLimitService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RateLimitDto> create(@Valid @RequestBody RateLimitDto rateLimitDto) {
        var createdRateLimit = rateLimitService.save(rateLimitDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRateLimit);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<RateLimitDto>> getAll(@RequestBody List<FilterDto> filters,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        var filteredRoutes = rateLimitService.getAll(page, size, filters);
        return ResponseEntity.ok(filteredRoutes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RateLimitDto> update(@RequestBody RateLimitDto rateLimitDto, @PathVariable Long id) {
        rateLimitDto.setId(id);
        RateLimitDto updatedRoute = rateLimitService.save(rateLimitDto);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RateLimitDto> delete(@PathVariable Long id) {
        rateLimitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<RateLimitDto> get(@PathVariable Long id) {
        var route = rateLimitService.getById(id);
        return ResponseEntity.ok(route);
    }
}

