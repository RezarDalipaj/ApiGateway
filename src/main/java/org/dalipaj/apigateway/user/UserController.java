package org.dalipaj.apigateway.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.common.PreAuthorizeConstants;
import org.dalipaj.apigateway.user.data.UserDto;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.filter.FilterDto;
import org.dalipaj.apigateway.common.exception.BadRequestException;
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
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") long id,
                                           HttpServletRequest request) throws UnAuthorizedException {
        return ResponseEntity.ok(userService.getById(id, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@Validated(OnUpdateGroup.class)
                                          @RequestBody UserDto userDto,
                                          @PathVariable("id") long id,
                                          HttpServletRequest request) throws BadRequestException,
                                                                             UnAuthorizedException {
        userDto.setId(id);
        return ResponseEntity.ok(userService.save(userDto, request));
    }

    @PutMapping("/{id}/scopes")
    @PreAuthorize(PreAuthorizeConstants.APPLICATION)
    public ResponseEntity<UserDto> addScopesToUser(@PathVariable("id") long id,
                                                   @RequestBody
                                                   @Valid @NotEmpty Set<String> scopes) {
        return ResponseEntity.ok(userService.addScopesToUser(id, scopes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id,
                                       HttpServletRequest request) throws BadRequestException,
                                                                          UnAuthorizedException {
        userService.delete(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize(PreAuthorizeConstants.ADMIN)
    public ResponseEntity<Page<UserDto>> getAll(@RequestBody(required = false)
                                                       @Valid List<FilterDto> filters,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.findAll(page, filters, size));
    }
}
