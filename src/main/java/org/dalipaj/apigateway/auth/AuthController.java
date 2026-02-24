package org.dalipaj.apigateway.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.data.LoginDto;
import org.dalipaj.apigateway.auth.data.TokenDto;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.application.data.ApplicationDto;
import org.dalipaj.apigateway.auth.service.IAuthService;
import org.dalipaj.apigateway.common.validation.OnCreateGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/applications")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@Validated(OnCreateGroup.class)
                                             @RequestBody ApplicationDto applicationDto,
                                             HttpServletRequest request) throws BadRequestException, UnAuthorizedException {
        return ResponseEntity.status(201).body(authService.saveApplication(applicationDto, request));
    }
}
