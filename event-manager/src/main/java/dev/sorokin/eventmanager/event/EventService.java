package dev.sorokin.eventmanager.event;

import dev.sorokin.eventcommon.kafka.NotificationChange;
import dev.sorokin.eventcommon.kafka.NotificationPayload;
import dev.sorokin.eventmanager.cache.CacheService;
import dev.sorokin.eventmanager.kafka.KafkaEventUpdatesCheck;
import dev.sorokin.eventmanager.kafka.KafkaSender;
import dev.sorokin.eventmanager.location.LocationEntity;
import dev.sorokin.eventmanager.location.LocationRepository;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.registration.RegistrationEntity;
import dev.sorokin.eventmanager.registration.RegistrationRepository;
import dev.sorokin.eventmanager.security.SecurityUtils;
import dev.sorokin.eventmanager.user.Role;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventService {

    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final KafkaEventUpdatesCheck kafkaEventUpdatesCheck;
    private final CacheService cacheService;
    private static final String REDIS_PREFIX = "event:";
    private final StringRedisTemplate stringRedisTemplate;


    public EventService(EventMapper eventMapper, LocationRepository locationRepository, EventRepository eventRepository, KafkaEventUpdatesCheck kafkaEventUpdatesCheck, CacheService cacheService, StringRedisTemplate stringRedisTemplate) {
        this.eventMapper = eventMapper;
        this.locationRepository = locationRepository;
        this.eventRepository=eventRepository;
        this.kafkaEventUpdatesCheck = kafkaEventUpdatesCheck;
        this.cacheService = cacheService;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Transactional
    public Event createEvent(Event eventToCreate) {

        Long currentUserId = SecurityUtils.getCurrentUserId();
        eventToCreate.setOwnerId(currentUserId);

        Long locationId = eventToCreate.getLocationId();
        LocationEntity location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found location with id: %s".formatted(locationId)));

        if (location.getCapacity() < eventToCreate.getMaxPlaces()) {
            throw new IllegalArgumentException(
                    "The capacity of the location (%d) is less than the number of maxPlaces (%d)"
                            .formatted(location.getCapacity(), eventToCreate.getMaxPlaces()));
        }

        EventEntity entity = eventMapper.toEntityFromEvent(eventToCreate);
        EventEntity savedEntity = eventRepository.save(entity);
        Event createdEvent = eventMapper.toEventFromEntity(savedEntity);

        String key = REDIS_PREFIX + savedEntity.getId();
        cacheService.writeEventToRedis(key, createdEvent);

        return createdEvent;
    }

    @Transactional
    public void deleteEventById(Long id) throws AccessDeniedException {
        EventEntity eventEntity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found event with id: %s".formatted(id)));

        if (!SecurityUtils.getCurrentUserId().equals(eventEntity.getOwnerId()) &&
                !SecurityUtils.getCurrentUser().getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Access denied");
        }

        if (!eventEntity.getStatus().equals(EventStatus.WAIT_START.name())) {
            throw new IllegalArgumentException("Event has already started or cancelled");
        }

        String key = REDIS_PREFIX + id;

        Event oldEventDto = eventMapper.toEventFromEntity(eventEntity);

        eventEntity.setStatus(EventStatus.CANCELLED.name());
        eventRepository.save(eventEntity);

        Event updatedEvent = eventMapper.toEventFromEntity(eventEntity);

        cacheService.writeEventToRedisIfPresent(key, updatedEvent);

        kafkaEventUpdatesCheck.publishEventUpdated(
                oldEventDto,
                updatedEvent,
                SecurityUtils.getCurrentUserId()
        );
    }

    @Transactional
    public Event getEventById(Long id) {

        String key = REDIS_PREFIX + id;

        Event eventFromCache = cacheService.readEventFromRedis(key);
        if (eventFromCache != null) {
            log.info("Get from Redis event successfully");
            return eventFromCache;
        }

        EventEntity foundEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found event with id: %s".formatted(id)));

        Event event = eventMapper.toEventFromEntity(foundEvent);

        cacheService.writeEventToRedis(key, event);

        return event;
    }

    @Transactional
    public Event updateEventById(Event eventToUpdate, Long id) {

        String lockedKey = REDIS_PREFIX + id;
        String token = UUID.randomUUID().toString();

        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockedKey, token, Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(locked)) {
            throw new AccessDeniedException("Event is being updated by another process");
        }

        Event updatedEvent;

        try {
            EventEntity existingEvent = eventRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Not found event with id: %s".formatted(id)));

            if (!SecurityUtils.getCurrentUserId().equals(existingEvent.getOwnerId())
                    && !SecurityUtils.getCurrentUser().getRole().equals(Role.ADMIN)) {
                throw new AccessDeniedException("Access denied");
            }

            int registrationsCount = eventRepository.countRegistrationsByEventId(id);

            if (registrationsCount > eventToUpdate.getMaxPlaces()) {
                throw new IllegalArgumentException(
                        String.format("Cannot reduce maxPlaces to %d because there are %d registrations already",
                                eventToUpdate.getMaxPlaces(), registrationsCount)
                );
            }

            LocationEntity location = locationRepository.findById(eventToUpdate.getLocationId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Not found location with id: %s".formatted(eventToUpdate.getLocationId())));

            if (location.getCapacity() < eventToUpdate.getMaxPlaces()) {
                throw new IllegalArgumentException(
                        "The capacity of the location (%d) is less than the number of maxPlaces (%d)"
                                .formatted(location.getCapacity(), eventToUpdate.getMaxPlaces()));
            }

            String key = REDIS_PREFIX + id;

            Event oldEvent = eventMapper.toEventFromEntity(existingEvent);

            existingEvent.setName(eventToUpdate.getName());
            existingEvent.setStartAt(eventToUpdate.getStartAt());
            existingEvent.setDurationMinutes(eventToUpdate.getDurationMinutes());
            existingEvent.setMaxPlaces(eventToUpdate.getMaxPlaces());
            existingEvent.setCost(eventToUpdate.getCost());
            existingEvent.setLocationId(eventToUpdate.getLocationId());

            EventEntity savedEntity = eventRepository.save(existingEvent);
            updatedEvent = eventMapper.toEventFromEntity(savedEntity);

            cacheService.writeEventToRedisIfPresent(key, updatedEvent);

            kafkaEventUpdatesCheck.publishEventUpdated(oldEvent, updatedEvent, SecurityUtils.getCurrentUserId());

        } finally {

            DefaultRedisScript<Long> unlockScript = new DefaultRedisScript<>();
            unlockScript.setScriptText("""
                if redis.call('get', KEYS[1]) == ARGV[1] then
                    return redis.call('del', KEYS[1])
                end
                return 0
                """);
            unlockScript.setResultType(Long.class);

            stringRedisTemplate.execute(unlockScript, List.of(lockedKey), token);
        }

        return updatedEvent;
    }

    public List<Event> getAllEventsByOwner() {

        Long ownerId=SecurityUtils.getCurrentUser().getId();
        List<Event>events=eventRepository.findByOwnerId(ownerId).stream()
                .map(eventEntity -> eventMapper.toEventFromEntity(eventEntity))
                .collect(Collectors.toList());

        return events;
    }

    public List<Event> getAllEventsByFilter(EventSearchRequest searchFromDto) {

        int pageSize = 1000;
        int pageNumber = 0;

        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<EventEntity>allEvents=eventRepository.searchAllByFilter(
                searchFromDto.getName(),
                searchFromDto.getPlaceMin(),
                searchFromDto.getPlaceMax(),
                searchFromDto.getDateStartAfter(),
                searchFromDto.getDateStartBefore(),
                searchFromDto.getCostMin(),
                searchFromDto.getCostMax(),
                searchFromDto.getDurationMin(),
                searchFromDto.getDurationMax(),
                searchFromDto.getLocationId(),
                searchFromDto.getStatus() != null ? searchFromDto.getStatus().name() : null,
                pageable
        );

        return allEvents.stream()
                .map(eventEntity -> eventMapper.toEventFromEntity(eventEntity))
                .collect(Collectors.toList());
    }
}
