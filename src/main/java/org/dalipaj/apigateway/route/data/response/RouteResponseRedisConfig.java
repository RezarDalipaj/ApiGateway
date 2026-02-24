package org.dalipaj.apigateway.route.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RouteResponseRedisConfig {

    @Bean
    public RedisTemplate<String, RouteRedisResponseWithMetadata> responseRedisTemplate(RedisConnectionFactory connectionFactory) {
        var template = new RedisTemplate<String, RouteRedisResponseWithMetadata>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        var ser = new Jackson2JsonRedisSerializer<>(mapper, RouteRedisResponseWithMetadata.class);
        template.setValueSerializer(ser);

        return template;
    }
}
