package org.dalipaj.apigateway.route;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.route.dto.RouteDto;
import org.dalipaj.apigateway.common.FilterDto;
import org.dalipaj.apigateway.route.service.IRouteService;
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
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final IRouteService routeService;

    @PostMapping
    public ResponseEntity<RouteDto> create(@Valid @RequestBody RouteDto routeDto,
                                               HttpServletRequest request) throws UnAuthorizedException {
        var createdRoute = routeService.save(routeDto, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<RouteDto>> getAll(@RequestBody List<FilterDto> filters,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        var filteredRoutes = routeService.getAll(page, size, filters);
        return ResponseEntity.ok(filteredRoutes);
    }

    @PutMapping("/{path}")
    public ResponseEntity<RouteDto> update(@RequestBody RouteDto routeDto,
                                           @PathVariable String path,
                                           HttpServletRequest request) throws UnAuthorizedException {
        routeDto.setPath(path);
        RouteDto updatedRoute = routeService.save(routeDto, request);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{path}")
    public ResponseEntity<RouteDto> delete(@PathVariable String path,
                                           HttpServletRequest request) throws UnAuthorizedException {
        routeService.delete(path, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{path}")
    public ResponseEntity<RouteDto> get(@PathVariable String path,
                                        HttpServletRequest request) throws UnAuthorizedException {
        var route = routeService.getByPath(path, request);
        return ResponseEntity.ok(route);
    }
}
