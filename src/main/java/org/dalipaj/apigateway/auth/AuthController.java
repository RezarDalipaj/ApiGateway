package org.dalipaj.apigateway.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.exception.custom.BadRequestException;
import org.dalipaj.apigateway.user.UserDto;
import org.dalipaj.apigateway.auth.service.IAuthService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@Valid @RequestBody UserDto userDto) throws BadRequestException {
        return ResponseEntity.status(201).body(authService.saveUser(userDto));
    }
}
