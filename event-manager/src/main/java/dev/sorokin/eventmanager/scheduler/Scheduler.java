package dev.sorokin.eventmanager.scheduler;

import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.event.EventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private final EventRepository eventRepository;

    public Scheduler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
                EventStatus.WAIT_START.name(),
                now
        );

        if (!eventsToStart.isEmpty()) {
            for (EventEntity event : eventsToStart) {
                event.setStatus(EventStatus.STARTED.name());
            }

            eventRepository.saveAll(eventsToStart);
            log.info("Updated {} events from WAIT_START to STARTED", eventsToStart.size());
        }
    }


    private void updateStartedToFinished() {
        LocalDateTime now = LocalDateTime.now();

        List<EventEntity> eventsToFinish = eventRepository.findAllByStatusAndEndTimeBefore(
                EventStatus.STARTED.name(),
                now
        );

        if (!eventsToFinish.isEmpty()) {
            for (EventEntity event : eventsToFinish) {
                event.setStatus(EventStatus.FINISHED.name());
            }
            eventRepository.saveAll(eventsToFinish);
            log.info("Updated {} events from STARTED to FINISHED", eventsToFinish.size());
        }
    }
}