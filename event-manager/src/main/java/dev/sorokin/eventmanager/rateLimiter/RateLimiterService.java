package dev.sorokin.eventmanager.rateLimiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;

    public RateLimiterService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean isAllowed(String clientId, int limit, Duration windowSize) {

        long now = Instant.now().getEpochSecond();
        long windowIndex = now / windowSize.getSeconds();

        String key = "rate:" + clientId + ":" + windowIndex;

        Long hits = stringRedisTemplate.opsForValue().increment(key);

        if (hits != null && hits == 1) {
            stringRedisTemplate.expire(key, windowSize);
        }

        return hits != null && hits <= limit;
    }
}
