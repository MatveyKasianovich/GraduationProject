package dev.sorokin.eventmanager.kafka;


import dev.sorokin.eventcommon.kafka.NotificationChange;
import dev.sorokin.eventcommon.kafka.NotificationPayload;
import dev.sorokin.eventmanager.event.Event;
import dev.sorokin.eventmanager.registration.RegistrationEntity;
import dev.sorokin.eventmanager.registration.RegistrationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class KafkaEventUpdatesCheck {

    private final RegistrationRepository registrationRepository;
    private final KafkaSender kafkaSender;

    public KafkaEventUpdatesCheck(RegistrationRepository registrationRepository, KafkaSender kafkaSender) {
        this.registrationRepository = registrationRepository;
        this.kafkaSender = kafkaSender;
    }

    public void publishEventUpdated(Event oldEvent, Event newEvent, Long changedById) {

        List<NotificationChange> changes = detectChanges(oldEvent, newEvent);

        if (changes.isEmpty()) {
            return;
        }

        List<Long> subscribers = registrationRepository.findUserIdsByEventId(newEvent.getId());

        NotificationPayload payload;
        String messageId = UUID.randomUUID().toString();

        if (!Objects.equals(oldEvent.getStatus(), newEvent.getStatus())) {
            payload = new NotificationPayload(
                    messageId,
                    "EVENT_CANCELLED",
                    newEvent.getId(),
                    LocalDateTime.now(),
                    changedById,
                    newEvent.getOwnerId(),
                    newEvent.getName(),
                    subscribers,
                    changes
            );
        }else {
            payload = new NotificationPayload(
                    messageId,
                    "EVENT_UPDATED",
                    newEvent.getId(),
                    LocalDateTime.now(),
                    changedById,
                    newEvent.getOwnerId(),
                    newEvent.getName(),
                    subscribers,
                    changes
            );
        }

        kafkaSender.sendNotification(payload);
    }

    private List<NotificationChange> detectChanges(Event oldEvent, Event newEvent) {
        List<NotificationChange> changes = new ArrayList<>();

        if (!Objects.equals(oldEvent.getName(), newEvent.getName())) {
            changes.add(new NotificationChange("name", oldEvent.getName(), newEvent.getName()));
        }
        if (!Objects.equals(oldEvent.getStartAt(), newEvent.getStartAt())) {
            changes.add(new NotificationChange("startAt", oldEvent.getStartAt(), newEvent.getStartAt()));
        }
        if (!Objects.equals(oldEvent.getDurationMinutes(), newEvent.getDurationMinutes())) {
            changes.add(new NotificationChange("durationMinutes", oldEvent.getDurationMinutes(), newEvent.getDurationMinutes()));
        }
        if (!Objects.equals(oldEvent.getMaxPlaces(), newEvent.getMaxPlaces())) {
            changes.add(new NotificationChange("maxPlaces", oldEvent.getMaxPlaces(), newEvent.getMaxPlaces()));
        }
        if (!Objects.equals(oldEvent.getCost(), newEvent.getCost())) {
            changes.add(new NotificationChange("cost", oldEvent.getCost(), newEvent.getCost()));
        }
        if (!Objects.equals(oldEvent.getLocationId(), newEvent.getLocationId())) {
            changes.add(new NotificationChange("locationId", oldEvent.getLocationId(), newEvent.getLocationId()));
        }
        if (!Objects.equals(oldEvent.getStatus(), newEvent.getStatus())) {
            changes.add(new NotificationChange("status", oldEvent.getStatus(), newEvent.getStatus()));
        }

        return changes;
    }
}
