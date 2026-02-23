package org.dalipaj.apigateway.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.application.ApplicationDto;
import org.dalipaj.apigateway.auth.LoginDto;
import org.dalipaj.apigateway.auth.TokenDto;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.dalipaj.apigateway.application.service.IApplicationService;
import org.dalipaj.apigateway.auth.service.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final IApplicationService applicationService;
    private final IHashService hashService;

    @Override
    public TokenDto login(LoginDto loginDto) {
        var saltedPassword = hashService.salt(loginDto.getPassword());
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getName(), saltedPassword));
        var accessToken = tokenProvider.generateAccessToken(authentication);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public TokenDto saveApplication(ApplicationDto applicationDto,
                                    HttpServletRequest request) throws BadRequestException, UnAuthorizedException {
        var savedApp = applicationService.save(applicationDto, request);
        var accessToken = tokenProvider.buildToken(savedApp);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
