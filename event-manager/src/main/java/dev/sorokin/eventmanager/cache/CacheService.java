package dev.sorokin.eventmanager.cache;

import dev.sorokin.eventmanager.event.Event;
import dev.sorokin.eventmanager.location.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class CacheService {

    private final RedisTemplate<String,Event> redisEventTemplate;
    private final RedisTemplate<String,Location> redisLocationTemplate;
    private static final String REDIS_LOCATION_PREFIX = "location:";
    private static final Duration REDIS_LOCATION_EXPIRATION = Duration.ofMinutes(1);


    public CacheService(RedisTemplate<String, Event> redisEventTemplate, RedisTemplate<String, Location> redisLocationTemplate) {
        this.redisEventTemplate = redisEventTemplate;
        this.redisLocationTemplate = redisLocationTemplate;
    }

    public Event readEventFromRedis(String key){
        try {
            return  redisEventTemplate.opsForValue().get(key);
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
            return null;
        }
    }

    public void writeEventToRedis(String key,Event event){
        try {
            redisEventTemplate.opsForValue().set(key,event, REDIS_LOCATION_EXPIRATION);
            log.info("Redis set event successfully");
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public void writeEventToRedisIfPresent(String key,Event event){
        try {
            redisEventTemplate.opsForValue().setIfPresent(key,event, REDIS_LOCATION_EXPIRATION);
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public Location readLocationFromRedis(String key){
        try {
            return  redisLocationTemplate.opsForValue().get(key);
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
            return null;
        }
    }

    public void writeLocationToRedis(String key,Location location){
        try {
            redisLocationTemplate.opsForValue().set(key,location,REDIS_LOCATION_EXPIRATION);
            log.info("Redis set location successfully");
        }catch (RedisConnectionFailureException e){
            log.error("Redis connection failure");
        }
    }

    public void writeLocationToRedisIfPresent(String key,Location location){
        try {
            redisLocationTemplate.opsForValue().set(key,location,REDIS_LOCATION_EXPIRATION);
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

    public List<Location> getAllLocationsFromCache() {
        try {
            return Optional.ofNullable(redisLocationTemplate.keys(REDIS_LOCATION_PREFIX + "*"))
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(key -> redisLocationTemplate.opsForValue().get(key))
                    .toList();

        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection failure");
            return null;
        }
    }

}