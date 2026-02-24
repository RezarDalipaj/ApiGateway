package org.dalipaj.apigateway.user;

import jakarta.servlet.http.HttpServletRequest;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.common.filter.FilterDto;
import org.dalipaj.apigateway.user.data.UserDto;
import org.dalipaj.apigateway.user.data.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

@Validated
public interface IUserService {

    UserEntity findByUsername(String username);

    Page<UserDto> findAll(int pageNumber, List<FilterDto> filters, int size);

    @Transactional
    UserDto save(UserDto user,
                 HttpServletRequest request) throws BadRequestException,
                                                    UnAuthorizedException;

    UserDto getById(long id,
                    HttpServletRequest request) throws UnAuthorizedException;

    @Transactional
    void delete(long id,
                HttpServletRequest request) throws UnAuthorizedException;

    UserDto getByName(String username);

    String getUsernameFromRequest(HttpServletRequest request) throws UnAuthorizedException;

    void checkUserPermissions(HttpServletRequest request,
                              String actualUsername) throws UnAuthorizedException;

    String hash(String raw);

    String sha256(String raw) throws NoSuchAlgorithmException;

    @Transactional
    UserDto addScopesToUser(Long userId,
                            Set<String> scopes);

}
