package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.data.EventStatus;
import dev.sorokin.eventmanager.dto.EventCreateRequestDTO;
import dev.sorokin.eventmanager.dto.EventResponseDTO;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entityToBusinnes.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {


    public Event toEventFromDto(EventCreateRequestDTO dto, Long ownerId) {
        return new Event(
                dto.getName(),
                dto.getStartAt(),
                dto.getDurationMinutes(),
                dto.getMaxPlaces(),
                dto.getLocationId(),
                0,
                EventStatus.WAIT_START.name(),
                dto.getCost(),
                ownerId
        );
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
        return new Event(
                entity.getId(),
                entity.getName(),
                entity.getStartAt(),
                entity.getDurationMinutes(),
                entity.getMaxPlaces(),
                entity.getLocationId(),
                entity.getOccupiedPlaces(),
                entity.getStatus(),
                entity.getCost(),
                entity.getOwnerId()
        );
    }


    public EventResponseDTO toResponseDtoFromEvent(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getStartAt(),
                event.getDurationMinutes(),
                event.getCost(),
                event.getMaxPlaces(),
                event.getOccupiedPlaces(),
                event.getLocationId(),
                event.getOwnerId(),
                event.getStatus()
        );
    }

}