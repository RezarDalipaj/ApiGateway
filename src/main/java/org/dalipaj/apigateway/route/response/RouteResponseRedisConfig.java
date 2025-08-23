package org.dalipaj.apigateway.route.response;

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

        var ser = new Jackson2JsonRedisSerializer<>(RouteRedisResponseWithMetadata.class);
        template.setValueSerializer(ser);

        template.afterPropertiesSet();
        return template;
    }
}
