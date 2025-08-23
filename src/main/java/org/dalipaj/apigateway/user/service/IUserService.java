package org.dalipaj.apigateway.user.service;

import org.dalipaj.apigateway.exception.custom.BadRequestException;
import org.dalipaj.apigateway.user.UserDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
public interface IUserService {

    @Transactional
    UserDto saveUser(UserDto user) throws BadRequestException;

    @Transactional
    void deleteUser(long id);

    UserDto getUserByUsername(String username);
}
