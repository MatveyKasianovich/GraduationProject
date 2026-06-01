package dev.sorokin.eventmanager.service;


import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.LocationRepository;
import dev.sorokin.eventmanager.entityToBusinnes.Location;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {

    private final LocationMapper mapper;
    private final LocationRepository locationRepository;

    public LocationService(LocationMapper mapper, LocationRepository locationRepository) {
        this.mapper = mapper;
        this.locationRepository = locationRepository;
    }


    public List<Location> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(mapper::toLocationFromEntity)
                .collect(Collectors.toList());
    }

    public Location getLocationById(Long id) {
        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }
        return mapper.toLocationFromEntity(locationRepository.getReferenceById(id));
    }

    @Transactional
    public Location updateLocation(Location locationToUpdate, Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }

        LocationEntity updatedEntity = new LocationEntity(
                id,
                locationToUpdate.getName(),
                locationToUpdate.getAddress(),
                locationToUpdate.getCapacity(),
                locationToUpdate.getDescription()
        );

        return mapper.toLocationFromEntity(locationRepository.save(updatedEntity));
    }

    @Transactional
    public Location createLocation(Location location) {
        return mapper.toLocationFromEntity(locationRepository.save(mapper.toEntityFromLocation(location)));
    }


    @Transactional
    public void deleteLocationById(Long id) {
        if(!locationRepository.existsById(id)){
            throw new EntityNotFoundException("No entity with id=%s".formatted(id));
        }
        locationRepository.deleteLocationEntitiesById(id);
    }
}
