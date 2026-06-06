package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.dto.EventSearchRequestDTO;
import dev.sorokin.eventmanager.dto.EventUpdateRequestDTO;
import dev.sorokin.eventmanager.event.EventSearchRequest;
import dev.sorokin.eventmanager.event.EventStatus;
import dev.sorokin.eventmanager.dto.EventCreateRequestDTO;
import dev.sorokin.eventmanager.dto.EventResponseDTO;
import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {


    public Event toEventFromDto(EventCreateRequestDTO dto) {
        Event event = new Event();
        event.setName(dto.getName());
        event.setStartAt(dto.getStartAt());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setMaxPlaces(dto.getMaxPlaces());
        event.setLocationId(dto.getLocationId());
        event.setCost(dto.getCost());
        event.setOccupiedPlaces(0);
        event.setStatus(EventStatus.WAIT_START.name());
        return event;
    }
    public Event toEventFromUpdateDTO(EventUpdateRequestDTO dto) {
        Event event = new Event();
        event.setName(dto.getName());
        event.setStartAt(dto.getStartAt());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setMaxPlaces(dto.getMaxPlaces());
        event.setLocationId(dto.getLocationId());
        event.setCost(dto.getCost());
        event.setOccupiedPlaces(0);
        return event;
    }


    public EventEntity toEntityFromEvent(Event event) {
        EventEntity entity = new EventEntity();
        entity.setId(event.getId());
        entity.setName(event.getName());
        entity.setStartAt(event.getStartAt());
        entity.setDurationMinutes(event.getDurationMinutes());
        entity.setMaxPlaces(event.getMaxPlaces());
        entity.setOccupiedPlaces(event.getOccupiedPlaces());
        entity.setStatus(event.getStatus());
        entity.setCost(event.getCost());
        entity.setLocationId(event.getLocationId());
        entity.setOwnerId(event.getOwnerId());
        return entity;
    }



    public Event toEventFromEntity(EventEntity entity) {
        Event event = new Event();
        event.setId(entity.getId());
        event.setName(entity.getName());
        event.setStartAt(entity.getStartAt());
        event.setDurationMinutes(entity.getDurationMinutes());
        event.setMaxPlaces(entity.getMaxPlaces());
        event.setLocationId(entity.getLocationId());
        event.setOccupiedPlaces(entity.getOccupiedPlaces());
        event.setStatus(entity.getStatus());
        event.setCost(entity.getCost());
        event.setOwnerId(entity.getOwnerId());

        return event;
    }


    public EventResponseDTO toResponseDtoFromEvent(Event event) {
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getStartAt());
        dto.setDuration(event.getDurationMinutes());
        dto.setCost(event.getCost());
        dto.setMaxPlaces(event.getMaxPlaces());
        dto.setOccupiedPlaces(event.getOccupiedPlaces());
        dto.setLocationId(event.getLocationId());
        dto.setOwnerId(event.getOwnerId());
        dto.setStatus(event.getStatus());

        return dto;
    }

    public EventSearchRequest toSearchFromDto(EventSearchRequestDTO eventSearchRequestDTO) {
        EventSearchRequest eventSearchRequest = new EventSearchRequest();
        eventSearchRequest.setName(eventSearchRequestDTO.name());
        eventSearchRequest.setPlaceMin(eventSearchRequestDTO.placeMin());
        eventSearchRequest.setPlaceMax(eventSearchRequestDTO.placeMax());
        eventSearchRequest.setDateStartAfter(eventSearchRequestDTO.dateStartAfter());
        eventSearchRequest.setDateStartBefore(eventSearchRequestDTO.dateStartBefore());
        eventSearchRequest.setCostMin(eventSearchRequestDTO.costMin());
        eventSearchRequest.setCostMax(eventSearchRequestDTO.costMax());
        eventSearchRequest.setDurationMin(eventSearchRequestDTO.durationMin());
        eventSearchRequest.setDurationMax(eventSearchRequestDTO.durationMax());
        eventSearchRequest.setLocationId(eventSearchRequestDTO.locationId());
        eventSearchRequest.setStatus(eventSearchRequestDTO.status());

        return eventSearchRequest;
    }

}