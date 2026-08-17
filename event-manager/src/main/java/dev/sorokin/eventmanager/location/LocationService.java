package dev.sorokin.eventmanager.location;


import dev.sorokin.eventmanager.cache.CacheService;
import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

        List<Location> cachedLocations = cacheService.getAllLocationsFromCache();

        if (cachedLocations.isEmpty()) {
            List<LocationEntity> entities = locationRepository.findAll();
            List<Location> locations = entities.stream()
                    .map(mapper::toLocationFromEntity)
                    .collect(Collectors.toList());
            locations.forEach(location -> cacheService.writeLocationToRedis(REDIS_PREFIX + location.getId(), location));
            return locations;
        }
        else {
            log.info("Got from Redis");
            return cachedLocations;
        }
    }

    public Location getLocationById(Long id) {

        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }

        String key = REDIS_PREFIX + id;
        Location locationFromCache = cacheService.readLocationFromRedis(key);
        if(locationFromCache != null){
            return locationFromCache;
        }

        LocationEntity locationEntityFromDB = locationRepository.getReferenceById(id);
        Location locationFromDB = mapper.toLocationFromEntity(locationEntityFromDB);

        cacheService.writeLocationToRedis(key, locationFromDB);

        return locationFromDB;
    }

    @Transactional
    public Location updateLocation(Location locationToUpdate, Long id) {

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

        Location savedLocation = mapper.toLocationFromEntity(locationRepository.save(updatedEntity));

        String key = REDIS_PREFIX + id;
        cacheService.writeLocationToRedisIfPresent(key, savedLocation);

        return savedLocation;
    }

    @Transactional
    public Location createLocation(Location location) {

        LocationEntity savedEntity = locationRepository.save(mapper.toEntityFromLocation(location));
        Location savedLocation = mapper.toLocationFromEntity(savedEntity);

        String key = REDIS_PREFIX + savedLocation.getId();
        cacheService.writeLocationToRedis(key, savedLocation);

        return savedLocation;
    }


    @Transactional
    public void deleteLocationById(Long id) {

        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }

        if(eventRepository.existsByLocationId(id)){
            throw new IllegalArgumentException("Location to delete has events");
        }

        locationRepository.deleteLocationEntitiesById(id);
        String key = REDIS_PREFIX + id;
        cacheService.deleteLocationFromRedis(key);
    }
}