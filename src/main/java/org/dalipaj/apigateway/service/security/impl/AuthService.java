package org.dalipaj.apigateway.service.security.impl;

import lombok.RequiredArgsConstructor;
import org.dalipaj.apigateway.exception.custom.BadRequestException;
import org.dalipaj.apigateway.model.dto.UserDto;
import org.dalipaj.apigateway.model.dto.request.LoginDto;
import org.dalipaj.apigateway.model.dto.response.TokenDto;
import org.dalipaj.apigateway.security.token.TokenProvider;
import org.dalipaj.apigateway.service.business.IUserService;
import org.dalipaj.apigateway.service.security.IAuthService;
import org.dalipaj.apigateway.util.security.PasswordUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final IUserService userService;

    @Override
    public TokenDto login(LoginDto loginDto) {
        var saltedPassword = PasswordUtil.getSaltedPassword(loginDto.getPassword());
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
