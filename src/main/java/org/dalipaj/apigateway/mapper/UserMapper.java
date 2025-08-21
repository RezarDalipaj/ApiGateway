package org.dalipaj.apigateway.mapper;

import org.dalipaj.apigateway.model.dto.UserDto;
import org.dalipaj.apigateway.model.entity.User;
import org.dalipaj.apigateway.util.mapper.MappingUtil;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.security.core.userdetails.UserDetails;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        uses = MappingUtil.class)
public interface UserMapper {

    @Mapping(source = "authorities", target = "role")
    UserDto userDetailsToUserDto(UserDetails userDetails);

    UserDto userToUserDto(User user);

    User userDtoToEntity(UserDto userDto);
}
