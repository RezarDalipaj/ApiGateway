package org.dalipaj.apigateway.rateLimit;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {
    
    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();

    public void allowRequest(String key, long limitPerMinute, long limitPerHour) throws RateLimitException {
        Counter counter = counters.computeIfAbsent(key, k -> new Counter());
        counter.incrementAndCheck(limitPerMinute, limitPerHour);
    }

    private static class Counter {
        private final AtomicInteger minuteCount = new AtomicInteger(0);
        private final AtomicInteger hourCount = new AtomicInteger(0);
        private long minuteWindow = Instant.now().getEpochSecond() / 60;
        private long hourWindow = Instant.now().getEpochSecond() / 3600;

        synchronized void incrementAndCheck(long perMinute, long perHour) throws RateLimitException {
            long nowMinute = Instant.now().getEpochSecond() / 60;
            long nowHour = Instant.now().getEpochSecond() / 3600;

            if (nowMinute != minuteWindow) {
                minuteCount.set(0);
                minuteWindow = nowMinute;
            }
            if (nowHour != hourWindow) {
                hourCount.set(0);
                hourWindow = nowHour;
            }

            int min = minuteCount.incrementAndGet();
            int hr = hourCount.incrementAndGet();

            if (min > perMinute)
                throw new RateLimitException("Rate limit per minute: " + perMinute + " exceeded");

            if (hr > perHour)
                throw  new RateLimitException("Rate limit per hour: " + perHour + " exceeded");
        }
    }
}

