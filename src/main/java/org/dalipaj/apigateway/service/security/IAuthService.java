package org.dalipaj.apigateway.service.security;

import org.dalipaj.apigateway.exception.custom.BadRequestException;
import org.dalipaj.apigateway.model.dto.UserDto;
import org.dalipaj.apigateway.model.dto.request.LoginDto;
import org.dalipaj.apigateway.model.dto.response.TokenDto;
import org.springframework.transaction.annotation.Transactional;

public interface IAuthService {

    TokenDto login(LoginDto loginDto);

    @Transactional
    TokenDto saveUser(UserDto userDto) throws BadRequestException;
}
