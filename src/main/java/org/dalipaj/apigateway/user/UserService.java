package org.dalipaj.apigateway.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.auth.UnAuthorizedException;
import org.dalipaj.apigateway.auth.service.IHashService;
import org.dalipaj.apigateway.auth.service.impl.TokenProvider;
import org.dalipaj.apigateway.common.exception.BadRequestException;
import org.dalipaj.apigateway.common.filter.FilterDto;
import org.dalipaj.apigateway.common.pagination.PaginationService;
import org.dalipaj.apigateway.upstream.data.backend.BackendRepository;
import org.dalipaj.apigateway.user.data.ScopeEntity;
import org.dalipaj.apigateway.user.data.ScopeRepository;
import org.dalipaj.apigateway.user.data.UserDto;
import org.dalipaj.apigateway.user.data.UserEntity;
import org.dalipaj.apigateway.user.data.UserRepository;
import org.dalipaj.apigateway.user.data.UserRole;
import org.dalipaj.apigateway.user.mapper.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends PaginationService implements IUserService {

    private final UserRepository userRepository;
    private final IHashService hashService;
    private final UserMapper userMapper;
    private final ScopeRepository scopeRepository;
    private final TokenProvider tokenProvider;
    private final BackendRepository backendRepository;
    private static final String ACCESS_DENIED_MESSAGE = "Access denied: Cannot access this resource";

    @Override
    public UserEntity findByUsername(String username) {
        var entity = userRepository.findByUsername(username);
        if (entity == null)
            throw new NullPointerException("User with name " + username + " does not exist");
        return entity;
    }

    @Override
    public Page<UserDto> findAll(int pageNumber, List<FilterDto> filters, int size) {
        var entityPages = super.getAll(pageNumber, size, filters, userRepository);
        return entityPages.map(userMapper::toUserDto);
    }

    @Override
    @Transactional
    public UserDto save(UserDto user,
                        HttpServletRequest request) throws BadRequestException,
                                                           UnAuthorizedException {
        if (user.getId() == null)
            return create(user);

        return update(user, request);
    }

    @Override
    public UserDto getById(long id,
                           HttpServletRequest request) throws UnAuthorizedException {
        var entity = findEntityById(id);
        checkUserPermissions(request, entity.getUsername());
        return userMapper.toUserDto(entity);
    }

    @Override
    @Transactional
    public void delete(long id,
                       HttpServletRequest request) throws UnAuthorizedException {
        var userEntity = findEntityById(id);
        checkUserPermissions(request, userEntity.getUsername());

        if (UserRole.ROLE_APPLICATION == userEntity.getRole())
            backendRepository.deleteRouteBackendsByAppId(userEntity.getId());

        userRepository.deleteById(id);
    }

    @Override
    public UserDto getByName(String username) {
        var user = findByUsername(username);
        return userMapper.toUserDto(user);
    }

    @Override
    public String getUsernameFromRequest(HttpServletRequest request) throws UnAuthorizedException {
        return tokenProvider.getUsernameFromRequest(request);
    }

    @Override
    public void checkUserPermissions(HttpServletRequest request,
                                     String actualUsername) throws UnAuthorizedException {
        if (!getUsernameFromRequest(request).equals(actualUsername))
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
    }

    @Override
    public String hash(String raw) {
        return hashService.hash(raw);
    }

    @Override
    public String sha256(String raw) throws NoSuchAlgorithmException {
        return hashService.sha256(raw);
    }

    @Override
    public UserDto addScopesToUser(Long userId,
                                   Set<String> scopes) {
        var userEntity = findEntityById(userId);
        if (UserRole.ROLE_USER != userEntity.getRole())
            throw new AccessDeniedException("Can only add scopes to users with role USER");

        var existingScopes = scopeRepository.findByNameIn(scopes);

        Set<String> existingScopeNames = existingScopes.stream()
                .map(ScopeEntity::getName)
                .collect(Collectors.toSet());

        List<ScopeEntity> newScopes = scopes.stream()
                .filter(scopeName -> !existingScopeNames.contains(scopeName))
                .map(scopeName -> {
                    var scopeEntity = new ScopeEntity();
                    scopeEntity.setName(scopeName);
                    return scopeRepository.save(scopeEntity);
                })
                .toList();

        userEntity.getScopes().addAll(existingScopes);
        userEntity.getScopes().addAll(newScopes);

        userEntity = userRepository.save(userEntity);
        return userMapper.toUserDto(userEntity);
    }

    private UserDto create(UserDto userDto) throws BadRequestException {
        validateUsernameIsUnique(userDto.getUsername());

        var userEntity = userMapper.toUserEntity(userDto);

        setPassword(userEntity, userDto.getPassword());
        userEntity = userRepository.save(userEntity);
        return userMapper.toUserDto(userEntity);
    }

    private UserDto update(UserDto userDto,
                           HttpServletRequest request) throws BadRequestException,
                                                              UnAuthorizedException {
        var userEntity = findEntityById(userDto.getId());
        checkUserPermissions(request, userEntity.getUsername());

        var username = userDto.getUsername();
        var rawPassword = userDto.getPassword();

        if (Strings.isNotBlank(username)) {
            validateUsernameIsUnique(username);
            userEntity.setUsername(username);
        }

        if (Strings.isNotBlank(rawPassword))
            setPassword(userEntity, rawPassword);

        var entity = userRepository.save(userEntity);
        return userMapper.toUserDto(entity);
    }

    private UserEntity findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("User with id ".concat(id.toString()).concat(" not found")));
    }

    private void setPassword(UserEntity user, String rawPassword) {
        user.setPassword(hash(rawPassword));
    }

    private void validateUsernameIsUnique(String username) throws BadRequestException {
        if (userRepository.existsByUsername(username))
            throw new BadRequestException("User already exists");
    }
}
