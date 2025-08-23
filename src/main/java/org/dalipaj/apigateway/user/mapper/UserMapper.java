package org.dalipaj.apigateway.user.mapper;

import org.dalipaj.apigateway.user.UserEntity;
import org.dalipaj.apigateway.user.UserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.security.core.userdetails.UserDetails;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        uses = UserMapperUtil.class)
public interface UserMapper {

    @Mapping(source = "authorities", target = "role")
    UserDto userDetailsToUserDto(UserDetails userDetails);

    UserDto userToUserDto(UserEntity user);

    UserEntity userDtoToEntity(UserDto userDto);
}
