package org.dalipaj.apigateway.service.business.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.dalipaj.apigateway.exception.custom.BadRequestException;
import org.dalipaj.apigateway.mapper.UserMapper;
import org.dalipaj.apigateway.model.dto.UserDto;
import org.dalipaj.apigateway.model.entity.User;
import org.dalipaj.apigateway.repository.UserRepository;
import org.dalipaj.apigateway.service.business.IUserService;
import org.dalipaj.apigateway.util.security.PasswordUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    private User findUserByUsername(String username) {
        var entity = userRepository.findByUsername(username);
        if (entity == null)
            throw new NullPointerException("User with username " + username + " does not exist");
        return entity;
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto userDto) throws BadRequestException {
        if (userDto.getId() == null)
            return save(userDto);
        return update(userDto);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    private UserDto save(UserDto userDto) throws BadRequestException {
        validateUsername(userDto.getUsername());
        validateEmail(userDto.getEmail());

        var user = userMapper.userDtoToEntity(userDto);
        setPassword(user, userDto.getPassword());
        var entity = userRepository.save(user);
        return userMapper.userToUserDto(entity);
    }

    private UserDto update(UserDto userDto) throws BadRequestException {
        var user = findEntityById(userDto.getId());
        var username = userDto.getUsername();
        var email = userDto.getEmail();
        var rawPassword = userDto.getPassword();

        if (Strings.isNotBlank(username)) {
            validateUsername(username);
            user.setEmail(username);
        }

        if (Strings.isNotBlank(email)) {
            validateEmail(email);
            user.setEmail(email);
        }

        if (Strings.isNotBlank(rawPassword))
            setPassword(user, rawPassword);

        var entity = userRepository.save(user);
        return userMapper.userToUserDto(entity);
    }

    private User findEntityById(Long id) {
        var user = userRepository.findById(id);
        if (user.isEmpty())
            throw new NullPointerException("User with id ".concat(id.toString()).concat(" not found"));
        return user.get();
    }

    private void setPassword(User user, String rawPassword) {
        var saltedPassword = PasswordUtil.getSaltedPassword(rawPassword);
        user.setPassword(passwordEncoder.encode(saltedPassword));
    }

    public void validateUsername(String username) throws BadRequestException {
        if (userRepository.existsByUsername(username))
            throw new BadRequestException("Username already exists");
    }

    public void validateEmail(String email) throws BadRequestException {
        if (userRepository.existsByEmail(email))
            throw new BadRequestException("Email already exists");
    }

    @Override
    public UserDto getUserByUsername(String username) {
        var user = findUserByUsername(username);
        return userMapper.userToUserDto(user);
    }

}
