package dev.sorokin.eventmanager.cache;

import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.location.LocationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class CacheService {

    private final RedisTemplate<String,EventEntity> redisEventTemplate;
    private final RedisTemplate<String,LocationEntity> redisLocationTemplate;
    private static final String REDIS_LOCATION_PREFIX = "location:";


    public CacheService(RedisTemplate<String, EventEntity> redisEventTemplate, RedisTemplate<String, LocationEntity> redisLocationTemplate) {
        this.redisEventTemplate = redisEventTemplate;
        this.redisLocationTemplate = redisLocationTemplate;
    }

    public EventEntity readEventFromRedis(String key){
        try {
            return  redisEventTemplate.opsForValue().get(key);
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
            return null;
        }
    }

    public void writeEventToRedis(String key,EventEntity eventEntity){
        try {
            redisEventTemplate.opsForValue().set(key,eventEntity, Duration.ofMinutes(1));
            log.info("Redis set event successfully");
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public void writeEventToRedisIfPresent(String key,EventEntity eventEntity){
        try {
            redisEventTemplate.opsForValue().setIfPresent(key,eventEntity, Duration.ofMinutes(1));
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public LocationEntity readLocationFromRedis(String key){
        try {
            return  redisLocationTemplate.opsForValue().get(key);
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
            return null;
        }
    }

    public void writeLocationToRedis(String key,LocationEntity locationEntity){
        try {
            redisLocationTemplate.opsForValue().set(key,locationEntity,Duration.ofMinutes(1));
            log.info("Redis set location successfully");
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public void writeLocationToRedisIfPresent(String key,LocationEntity locationEntity){
        try {
            redisLocationTemplate.opsForValue().set(key,locationEntity,Duration.ofMinutes(1));
            log.info("Redis set successfully");
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public void deleteLocationFromRedis(String key){
        try {
            redisLocationTemplate.delete(key);
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public List<LocationEntity> getAllLocationsFromCache() {
        try {
            List<LocationEntity> entities = redisLocationTemplate.keys(REDIS_LOCATION_PREFIX + "*").stream()
                    .map(key -> redisLocationTemplate.opsForValue().get(key))
                    .toList();
            return entities;

        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure");
            return null;
        }
    }

}
