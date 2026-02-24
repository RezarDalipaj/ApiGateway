package org.dalipaj.apigateway.application.mapper;

import org.dalipaj.apigateway.application.data.ApplicationDto;
import org.dalipaj.apigateway.application.data.ApplicationEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.security.core.userdetails.UserDetails;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        uses = ApplicationMapperUtil.class)
public interface ApplicationMapper {

    @Mapping(source = "authorities", target = "role")
    @Mapping(source = "username", target = "name")
    @Mapping(target = "id", ignore = true)
    ApplicationDto toAppDto(UserDetails userDetails);

    ApplicationDto toAppDto(ApplicationEntity applicationEntity);

    @Mapping(target = "rateLimits", ignore = true)
    @Mapping(target = "services", ignore = true)
    ApplicationEntity toAppEntity(ApplicationDto applicationDto);
}
