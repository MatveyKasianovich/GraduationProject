package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.EventRepository;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.LocationRepository;
import dev.sorokin.eventmanager.entityToBusinnes.Event;
import dev.sorokin.eventmanager.entityToBusinnes.Location;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;


    public EventService(EventMapper eventMapper, LocationRepository locationRepository, EventRepository eventRepository) {
        this.eventMapper = eventMapper;
        this.locationRepository = locationRepository;
        this.eventRepository=eventRepository;
    }

    @Transactional
    public Event createEvent(Event eventToCreate) {
        // 1. Получаем текущего пользователя из контекста безопасности
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 2. Устанавливаем ownerId для события
        eventToCreate.setOwnerId(currentUserId);

        // 3. Проверяем существование локации
        Long locationId = eventToCreate.getLocationId();
        LocationEntity location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found location with id: %s".formatted(locationId)));

        // 4. Проверяем вместимость локации
        if (location.getCapacity() < eventToCreate.getMaxPlaces()) {
            throw new IllegalArgumentException(
                    "The capacity of the location (%d) is less than the number of maxPlaces (%d)"
                            .formatted(location.getCapacity(), eventToCreate.getMaxPlaces()));
        }

        // 5. Преобразуем Event -> EventEntity
        EventEntity entity = eventMapper.toEntityFromEvent(eventToCreate);

        // 6. Сохраняем в базу
        EventEntity savedEntity = eventRepository.save(entity);

        // 7. Возвращаем сохраненное событие как бизнес-объект
        return eventMapper.toEventFromEntity(savedEntity);
    }
}
