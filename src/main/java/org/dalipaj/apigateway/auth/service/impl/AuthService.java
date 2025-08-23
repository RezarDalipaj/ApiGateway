package org.dalipaj.apigateway.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.exception.custom.BadRequestException;
import org.dalipaj.apigateway.user.UserDto;
import org.dalipaj.apigateway.auth.LoginDto;
import org.dalipaj.apigateway.auth.TokenDto;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.dalipaj.apigateway.user.service.IUserService;
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
    public TokenDto saveUser(UserDto userDto) throws BadRequestException {
        var user = userService.saveUser(userDto);
        var accessToken = tokenProvider.buildToken(user);

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
