package dev.sorokin.eventnotificator.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class CacheService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String REDIS_PREFIX = "notif:unread:";
    private static final Duration duration=Duration.ofDays(1);

    public CacheService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void incrementCacheValue(String userId){

        String key = REDIS_PREFIX + userId;

        try {
            if(stringRedisTemplate.hasKey(key)){
                stringRedisTemplate.opsForValue().increment(REDIS_PREFIX+userId);
            }else {
                stringRedisTemplate.opsForValue().set(key,"1", duration);
            }
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public void decrementCacheValue(String userId,Long amountOfUnreadNotifications){
        String key = REDIS_PREFIX + userId;
        try {
            stringRedisTemplate.opsForValue().set(key,amountOfUnreadNotifications.toString(),duration);
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }
}
