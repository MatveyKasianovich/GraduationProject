package dev.sorokin.eventmanager.scheduler;

import dev.sorokin.eventcommon.kafka.NotificationChange;
import dev.sorokin.eventcommon.kafka.NotificationPayload;
import dev.sorokin.eventmanager.cache.CacheService;
import dev.sorokin.eventmanager.event.Event;
import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.kafka.KafkaSender;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.registration.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static dev.sorokin.eventmanager.event.EventStatus.*;

@Component
@EnableScheduling
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final EventMapper eventMapper;
    private final KafkaSender kafkaSender;
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final CacheService cacheService;
    private static final String REDIS_PREFIX = "event:";

    public Scheduler(EventMapper eventMapper, KafkaSender kafkaSender, RegistrationRepository registrationRepository, EventRepository eventRepository, CacheService cacheService) {
        this.eventMapper = eventMapper;
        this.kafkaSender = kafkaSender;
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.cacheService = cacheService;
    }

    /**
     * Запускается каждую минуту и переводит статусы событий:
     * WAIT_START -> STARTED
     * STARTED -> FINISHED
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateEventStatuses() {
        updateWaitingToStarted();
        updateStartedToFinished();
    }


    private void updateWaitingToStarted() {

        LocalDateTime now = LocalDateTime.now();

        List<EventEntity> eventsToStart = eventRepository.findAllByStatusAndStartAtBefore(
                WAIT_START.name(),
                now
        );
        if (!eventsToStart.isEmpty()) {
            for (EventEntity eventEntity : eventsToStart) {

                Event oldEventDto = eventMapper.toEventFromEntity(eventEntity);
                EventEntity oldEvent = eventMapper.toEntityFromEvent(oldEventDto);
                eventEntity.setStatus(STARTED.name());


                Event event = eventMapper.toEventFromEntity(eventEntity);
                cacheService.writeEventToRedisIfPresent(REDIS_PREFIX + eventEntity.getId(),event);

                publishNotificationToKafka(oldEvent, eventEntity);
            }

            eventRepository.saveAll(eventsToStart);
            log.info("Updated {} events from WAIT_START to STARTED", eventsToStart.size());
        }
    }


    private void updateStartedToFinished() {

        LocalDateTime now = LocalDateTime.now();

        List<EventEntity> eventsToFinish = eventRepository.findAllByStatus(STARTED.name());

        if (!eventsToFinish.isEmpty()) {
            for (EventEntity eventEntity : eventsToFinish) {
                LocalDateTime endTime = eventEntity.getStartAt().plusMinutes(eventEntity.getDurationMinutes());
                if (endTime.isBefore(now) || endTime.isEqual(now)) {
                    Event oldEventDto = eventMapper.toEventFromEntity(eventEntity);
                    EventEntity oldEvent = eventMapper.toEntityFromEvent(oldEventDto);
                    eventEntity.setStatus(FINISHED.name());

                    Event event = eventMapper.toEventFromEntity(eventEntity);
                    cacheService.writeEventToRedisIfPresent(REDIS_PREFIX+event.getId(),event);

                    publishNotificationToKafka(oldEvent,eventEntity);
                }
            }
            eventRepository.saveAll(eventsToFinish);
            log.info("Updated {} events from STARTED to FINISHED", eventsToFinish.size());
        }
    }

    private void publishNotificationToKafka(EventEntity oldEvent, EventEntity newEvent) {

        String messageId = UUID.randomUUID().toString();

        List<NotificationChange> changes = List.of(
                new NotificationChange("status", oldEvent.getStatus(), newEvent.getStatus())
        );

        List<Long> subscribers = registrationRepository.findUserIdsByEventId(newEvent.getId());

        String eventType = switch (newEvent.getStatus()) {
            case "STARTED" -> "EVENT_STARTED";
            case "FINISHED" -> "EVENT_FINISHED";
            default -> "EVENT_STATUS_CHANGED";
        };

        NotificationPayload payload = new NotificationPayload(
                messageId,
                eventType,
                newEvent.getId(),
                LocalDateTime.now(),
                null,
                newEvent.getOwnerId(),
                newEvent.getName(),
                subscribers,
                changes
        );

        kafkaSender.sendNotification(payload);
    }
}
