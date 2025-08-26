package org.dalipaj.apigateway.auth.service;

import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.user.UserDto;
import org.dalipaj.apigateway.auth.LoginDto;
import org.dalipaj.apigateway.auth.TokenDto;
import org.springframework.transaction.annotation.Transactional;

public interface IAuthService {

    TokenDto login(LoginDto loginDto);

    @Transactional
    TokenDto saveUser(UserDto userDto) throws BadRequestException;
}
