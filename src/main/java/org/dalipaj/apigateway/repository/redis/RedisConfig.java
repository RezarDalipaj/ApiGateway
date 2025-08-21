package org.dalipaj.apigateway.repository.redis;

import org.dalipaj.apigateway.model.entity.ApiKey;
import org.dalipaj.apigateway.model.entity.Backend;
import org.dalipaj.apigateway.model.entity.RateLimit;
import org.dalipaj.apigateway.model.entity.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RedisConfig {

    // Pub/Sub: listen to channel "routes:changed"
    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    public RedisTemplate<String, Route> routeRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getRedisTemplate(redisConnectionFactory, Route.class);
    }

    @Bean
    public RedisTemplate<String, Backend> backendRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getRedisTemplate(redisConnectionFactory, Backend.class);
    }

    @Bean
    public RedisTemplate<String, ApiKey> apiKeyRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getRedisTemplate(redisConnectionFactory, ApiKey.class);
    }

    @Bean
    public RedisTemplate<String, RateLimit> rateLimitRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return getRedisTemplate(redisConnectionFactory, RateLimit.class);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    private <T> RedisTemplate<String, T> getRedisTemplate(RedisConnectionFactory connectionFactory, Class<T> entityClass) {
        var template = new RedisTemplate<String, T>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        var ser = new Jackson2JsonRedisSerializer<>(entityClass);
        template.setValueSerializer(ser);

        template.afterPropertiesSet();
        return template;
    }
}
