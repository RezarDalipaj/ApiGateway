package org.dalipaj.apigateway.user.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.user.UserDto;
import org.dalipaj.apigateway.user.UserEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface IUserService {

    UserEntity findUserByUsername(String username);

    @Transactional
    UserDto saveUser(UserDto user) throws BadRequestException;

    @Transactional
    void deleteUser(long id);

    UserDto getUserByUsername(String username);

    String getUsernameFromRequest(HttpServletRequest request) throws UnAuthorizedException;

    void validateUsername(String usernameFromRequest, String actualUsername);

    String salt(String raw);

    String encode(String raw);
}
