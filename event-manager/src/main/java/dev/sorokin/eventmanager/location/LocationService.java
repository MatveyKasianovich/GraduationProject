package dev.sorokin.eventmanager.location;


import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {

    private final EventRepository eventRepository;
    private final LocationMapper mapper;
    private final LocationRepository locationRepository;

    public LocationService(EventRepository eventRepository, LocationMapper mapper, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
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

        if(eventRepository.existsByLocationId(id)){
            throw new IllegalArgumentException("Location to delete has events");
        }
        locationRepository.deleteLocationEntitiesById(id);
    }
}
