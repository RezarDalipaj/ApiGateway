package org.dalipaj.apigateway.user.mapper;

import org.dalipaj.apigateway.user.data.UserDto;
import org.dalipaj.apigateway.user.data.UserEntity;
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
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scopes", ignore = true)
    UserDto toUserDto(UserDetails userDetails);

    @Mapping(source = "scopes", target = "scopes", qualifiedByName = UserMapperUtil.TO_DTO_SCOPES)
    UserDto toUserDto(UserEntity userEntity);

    @Mapping(target = "rateLimits", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "scopes", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserEntity toUserEntity(UserDto userDto);
}
