package org.dalipaj.apigateway.rateLimit.service;

import org.dalipaj.apigateway.rateLimit.data.RateLimitDto;
import org.dalipaj.apigateway.rateLimit.data.RateLimitEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface RateLimitMapper {

    @Mapping(source = "application.username", target = "applicationName")
    @Mapping(source = "apiKey.lookupKey", target = "apiKey")
    RateLimitDto toDto(RateLimitEntity rateLimit);

    @Mapping(target = "application", ignore = true)
    @Mapping(target = "apiKey", ignore = true)
    RateLimitEntity toEntity(RateLimitDto rateLimitDto);
}
