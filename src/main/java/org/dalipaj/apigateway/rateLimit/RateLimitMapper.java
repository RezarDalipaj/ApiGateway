package org.dalipaj.apigateway.rateLimit;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface RateLimitMapper {

    @Mapping(source = "user.username", target = "username")
    RateLimitDto toDto(RateLimitEntity rateLimit);

    @Mapping(target = "user", ignore = true)
    RateLimitEntity toEntity(RateLimitDto rateLimitDto);
}
