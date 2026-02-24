package org.dalipaj.apigateway.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.user.data.UserDto;
import org.dalipaj.apigateway.auth.data.LoginDto;
import org.dalipaj.apigateway.auth.data.TokenDto;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.dalipaj.apigateway.user.IUserService;
import org.dalipaj.apigateway.auth.service.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final IUserService userService;
    private final IHashService hashService;

    @Override
    public TokenDto login(LoginDto loginDto) {
        var saltedPassword = hashService.salt(loginDto.getPassword());
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), saltedPassword));
        var accessToken = tokenProvider.generateAccessToken(authentication);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    public TokenDto saveUser(UserDto userDto,
                             HttpServletRequest request) throws BadRequestException,
                                                                UnAuthorizedException {
        var savedApp = userService.save(userDto, request);
        var accessToken = tokenProvider.buildToken(savedApp);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
