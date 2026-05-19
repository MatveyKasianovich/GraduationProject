package dev.sorokin.eventmanager.service;


import dev.sorokin.eventmanager.dto.LocationDTO;
import dev.sorokin.eventmanager.entity.LocationRepository;
import dev.sorokin.eventmanager.mapper.Mapper;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {

    private final Mapper mapper;
    private final LocationRepository locationRepository;

    public LocationService(Mapper mapper, LocationRepository locationRepository) {
        this.mapper = mapper;
        this.locationRepository = locationRepository;
    }


    public List<Location> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(it->mapper.toLocationFromEntity(it))
                .collect(Collectors.toList());
    }

    public Location getLocationById(Long id) {
        if(!locationRepository.existsById(id)){
            throw new NoSuchElementException("No entity with id=%s".formatted(id));
        }
        return mapper.toLocationFromEntity(locationRepository.getReferenceById(id));
    }

    @Transactional
    public Location createLocation(Location location) {
        return mapper.toLocationFromEntity(locationRepository.save(mapper.toEntityFromLocation(location)));
    }


    @Transactional
    public void deleteLocationById(Long id) {
        if(!locationRepository.existsById(id)){
            throw new NoSuchElementException("No entity with id=%s".formatted(id));
        }
        locationRepository.deleteLocationEntitiesById(id);
    }
}
