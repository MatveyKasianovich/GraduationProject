package dev.sorokin.eventmanager.location;


import dev.sorokin.eventmanager.cache.CacheService;
import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LocationService {

    private final EventRepository eventRepository;
    private final LocationMapper mapper;
    private final LocationRepository locationRepository;
    private static final String REDIS_PREFIX = "location:";
    private final CacheService cacheService;

    public LocationService(EventRepository eventRepository, LocationMapper mapper, LocationRepository locationRepository, CacheService cacheService) {
        this.eventRepository = eventRepository;
        this.mapper = mapper;
        this.locationRepository = locationRepository;
        this.cacheService = cacheService;
    }

    public List<Location> getAllLocations() {

        List<LocationEntity> locationEntities = cacheService.getAllLocationsFromCache();

        if (locationEntities.isEmpty()) {
            List<LocationEntity> entities = locationRepository.findAll();
            entities.stream().forEach(locationEntity -> {cacheService.writeLocationToRedis(REDIS_PREFIX+locationEntity.getId(), locationEntity);});
            return entities.stream()
                    .map(mapper::toLocationFromEntity)
                    .collect(Collectors.toList());
        }
        else {
            log.info("Got from Redis");
            return  locationEntities.stream()
                .map(entity->mapper.toLocationFromEntity(entity))
                .collect(Collectors.toList());
        }
    }

    public Location getLocationById(Long id) {

        String key = REDIS_PREFIX + id;

        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }

        LocationEntity locationEntityFromCache=cacheService.readLocationFromRedis(key);
        if(locationEntityFromCache!=null){
            return mapper.toLocationFromEntity(locationEntityFromCache);
        }

        LocationEntity locationEntityFromDB=locationRepository.getReferenceById(id);

        cacheService.writeLocationToRedis(key,locationEntityFromDB);

        return mapper.toLocationFromEntity(locationEntityFromDB);
    }

    @Transactional
    public Location updateLocation(Location locationToUpdate, Long id) {

        String key = REDIS_PREFIX + id;

        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }

        List<EventEntity>placesMoreThanInLocationToUpdate=eventRepository.findAllByLocationIdWithMaxPlaces(id,locationToUpdate.getCapacity());
        if(!placesMoreThanInLocationToUpdate.isEmpty()){
            throw new IllegalArgumentException("Events on this location should have more places than in your locationToUpdate");
        }

        LocationEntity updatedEntity = new LocationEntity(
                id,
                locationToUpdate.getName(),
                locationToUpdate.getAddress(),
                locationToUpdate.getCapacity(),
                locationToUpdate.getDescription()
        );

        cacheService.writeLocationToRedisIfPresent(key, updatedEntity);

        return mapper.toLocationFromEntity(locationRepository.save(updatedEntity));
    }

    @Transactional
    public Location createLocation(Location location) {
        String key = REDIS_PREFIX + location.getId();

        LocationEntity locationEntity = locationRepository.save(mapper.toEntityFromLocation(location));
        cacheService.writeLocationToRedis(key, locationEntity);

        return mapper.toLocationFromEntity(locationEntity);
    }


    @Transactional
    public void deleteLocationById(Long id) {
        String key = REDIS_PREFIX + id;

        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }

        if(eventRepository.existsByLocationId(id)){
            throw new IllegalArgumentException("Location to delete has events");
        }
        locationRepository.deleteLocationEntitiesById(id);
        cacheService.deleteLocationFromRedis(key);
    }


}
