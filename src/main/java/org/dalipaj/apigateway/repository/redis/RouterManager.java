package org.dalipaj.apigateway.repository.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dalipaj.apigateway.model.entity.Route;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.*;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
public class RouterManager implements SmartLifecycle {

    private final RedisTemplate<String, Route> routeRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer listenerContainer;

    private volatile RadixRouter router = new RadixRouter();
    private volatile boolean running = false;

    /** Load all routes from Redis and rebuild the in-memory router. */
    public synchronized void rebuild() {
        Set<String> keys = routeRedisTemplate.keys("route*");
        var newRouter = new RadixRouter();

        if (!keys.isEmpty()) {
            Objects.requireNonNull(routeRedisTemplate.opsForValue()
                            .multiGet(keys))
                    .forEach(route -> router.add(route));
        }

        router = newRouter; // atomic swap
        log.info("Router rebuilt with {} routes", keys.size());
    }

    /** Force rebuild on schedule (fallback if Pub/Sub is down) */
    @Scheduled(fixedDelay = 15000)
    public void periodicRebuild() {
        rebuild();
    }

    /** Pub/Sub hot reload: subscribe to "routes:changed" */
    @Override
    public void start() {
        rebuild();
        var adapter = new MessageListenerAdapter((MessageListener) (message, pattern) -> {
            log.info("Received routes:changed — rebuilding router");
            rebuild();
        });

        listenerContainer.addMessageListener(adapter, new PatternTopic("routes:changed"));
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}

