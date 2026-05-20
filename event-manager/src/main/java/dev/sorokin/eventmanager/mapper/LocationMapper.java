package dev.sorokin.eventmanager.mapper;


import dev.sorokin.eventmanager.dto.LocationDTO;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.service.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toLocationFromDto(LocationDTO locationDTO){
        return new Location(locationDTO.id(),locationDTO.name(), locationDTO.address(), locationDTO.capacity(), locationDTO.description());
    }

    public LocationDTO toLocationDtoFromLocation(Location  location){
        return new LocationDTO(location.getId(), location.getName(), location.getAddress(), location.getCapacity(),location.getDescription());
    }

    public LocationEntity toEntityFromLocation(Location location){
        return new LocationEntity(location.getId(), location.getName(), location.getAddress(), location.getCapacity(), location.getDescription());
    }

    public Location toLocationFromEntity(LocationEntity entity){
        return new Location(entity.getId(), entity.getName(), entity.getAddress(),entity.getCapacity(),entity.getDescription());
    }
}
