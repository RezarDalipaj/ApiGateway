package org.dalipaj.apigateway.rateLimit;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface RateLimitMapper {

    RateLimitDto toDto(RateLimitEntity rateLimit);

    RateLimitEntity toEntity(RateLimitDto rateLimitDto);
}
