package org.dalipaj.apigateway.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.data.LoginDto;
import org.dalipaj.apigateway.auth.data.TokenDto;
import org.dalipaj.apigateway.auth.service.IAuthService;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.dalipaj.apigateway.user.data.UserDto;
import org.dalipaj.apigateway.user.data.UserRole;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/applications/register")
    public ResponseEntity<TokenDto> registerApp(@Validated(OnCreateGroup.class)
                                                @RequestBody UserDto userDto,
                                                HttpServletRequest request) throws BadRequestException,
                                                                                   UnAuthorizedException {
        userDto.setRole(UserRole.ROLE_APPLICATION.toString());
        return ResponseEntity.status(201).body(authService.saveUser(userDto, request));
    }

    @PostMapping("/users/register")
    public ResponseEntity<TokenDto> registerUser(@Validated(OnCreateGroup.class)
                                                 @RequestBody UserDto userDto,
                                                 HttpServletRequest request) throws BadRequestException,
                                                                                    UnAuthorizedException {
        userDto.setRole(UserRole.ROLE_USER.toString());
        return ResponseEntity.status(201).body(authService.saveUser(userDto, request));
    }
}
