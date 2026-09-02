package dev.sorokin.eventmanager;


import dev.sorokin.eventmanager.cache.CacheService;
import dev.sorokin.eventmanager.event.Event;
import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.event.EventService;
import dev.sorokin.eventmanager.kafka.KafkaEventUpdatesCheck;
import dev.sorokin.eventmanager.location.LocationRepository;
import dev.sorokin.eventmanager.mapper.EventMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Чистые юнит-тесты EventService: без Spring-контекста, все зависимости замоканы.
 * Проверяется только логика метода getEventById (кэш-хит / кэш-мисс / отсутствие сущности),
 * так как остальные методы сервиса используют статический SecurityUtils и требуют
 * либо интеграционного окружения, либо mockStatic.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventMapper eventMapper;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private KafkaEventUpdatesCheck kafkaEventUpdatesCheck;
    @Mock
    private CacheService cacheService;
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    private EventService eventService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        eventService = new EventService(eventMapper, locationRepository, eventRepository,
                kafkaEventUpdatesCheck, cacheService, stringRedisTemplate);
    }

    @Test
    void getEventById_returnsEventFromCache_whenCacheHit() {

        Long id = 1L;
        Event cachedEvent = new Event("Conference", null, 60, 10, 5L, 0, "WAIT_START", 100);
        cachedEvent.setId(id);

        when(cacheService.readEventFromRedis("event:" + id)).thenReturn(cachedEvent);

        Event result = eventService.getEventById(id);

        assertThat(result).isEqualTo(cachedEvent);
        verifyNoInteractions(eventRepository);
        verify(cacheService, never()).writeEventToRedis(any(), any());
    }

    @Test
    void getEventById_fetchesFromRepositoryAndCache_whenCacheMiss() {

        Long id = 2L;
        EventEntity entity = new EventEntity("Meetup", null, 30, 20, 0, "WAIT_START");
        entity.setId(id);
        Event mappedEvent = new Event("Meetup", null, 30, 20, 3L, 0, "WAIT_START", 0);
        mappedEvent.setId(id);

        when(cacheService.readEventFromRedis("event:" + id)).thenReturn(null);
        when(eventRepository.findById(id)).thenReturn(Optional.of(entity));
        when(eventMapper.toEventFromEntity(entity)).thenReturn(mappedEvent);

        Event result = eventService.getEventById(id);

        assertThat(result).isEqualTo(mappedEvent);
        verify(cacheService).writeEventToRedis(eq("event:" + id), eq(mappedEvent));
    }

    @Test
    void getEventById_throwsEntityNotFoundException_whenEventDoesNotExist() {

        Long id = 3L;
        when(cacheService.readEventFromRedis("event:" + id)).thenReturn(null);
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.valueOf(id));

        verify(cacheService, never()).writeEventToRedis(any(), any());
    }
}
