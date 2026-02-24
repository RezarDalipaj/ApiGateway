package org.dalipaj.apigateway.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.user.data.UserDto;
import org.dalipaj.apigateway.auth.data.LoginDto;
import org.dalipaj.apigateway.auth.data.TokenDto;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface IAuthService {

    TokenDto login(LoginDto loginDto);

    @Transactional
    TokenDto saveUser(UserDto userDto,
                      HttpServletRequest request) throws BadRequestException,
                                                         UnAuthorizedException;
}
