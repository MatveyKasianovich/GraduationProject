package dev.sorokin.eventmanager.controller;


import dev.sorokin.eventmanager.dto.LocationDTO;
import dev.sorokin.eventmanager.mapper.Mapper;
import dev.sorokin.eventmanager.service.Location;
import dev.sorokin.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
public class LocationController {


    private final Mapper mapper;
    private final LocationService locationService;

    public LocationController(Mapper mapper, LocationService locationService) {
        this.mapper = mapper;
        this.locationService = locationService;
    }


    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations(){
        return ResponseEntity.status(HttpStatus.OK).body(
                locationService.getAllLocations().stream()
                    .map(it->mapper.toLocationDtoFromLocation(it))
                    .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toLocationDtoFromLocation(locationService.getLocationById(id)));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody @Valid LocationDTO locationDTO){
        Location locationToCreate=mapper.toLocationFromDto(locationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toLocationDtoFromLocation(locationService.createLocation(locationToCreate)));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> deleteLocationById(@PathVariable Long id){
        locationService.deleteLocationById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
