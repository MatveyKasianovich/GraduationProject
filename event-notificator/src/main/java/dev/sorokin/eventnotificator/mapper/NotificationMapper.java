package dev.sorokin.eventnotificator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventcommon.kafka.NotificationChange;
import dev.sorokin.eventcommon.kafka.NotificationPayload;
import dev.sorokin.eventnotificator.entities.NotificationEntity;
import dev.sorokin.eventnotificator.entities.NotificationEventPayloadEntity;
import dev.sorokin.eventnotificator.notification.Notification;
import dev.sorokin.eventnotificator.notification.NotificationResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    private final ObjectMapper objectMapper;

    public NotificationMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public NotificationResponse toResponse(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getPayload().getEventType(),
                notification.getPayload().getEventId(),
                notification.getCreatedAt(),
                notification.getRead(),
                generateMessage(notification),
                notification.getPayload()
        );
    }


    public NotificationPayload toPayload(NotificationEventPayloadEntity entity) {

        NotificationPayload payload = new NotificationPayload();
        payload.setMessageId(entity.getMessageId());
        payload.setEventType(entity.getEventType());
        payload.setEventId(entity.getEventId());
        payload.setOccurredAt(entity.getOccurredAt());
        payload.setOwnerId(entity.getOwnerId());
        payload.setChangeById(entity.getChangedById());
        payload.setEventName(entity.getEventName());


        if (entity.getChanges() != null && !entity.getChanges().isBlank()) {
            try {
                List<NotificationChange> changes = objectMapper.readValue(
                        entity.getChanges(),
                        new TypeReference<List<NotificationChange>>() {}
                );
                payload.setChanges(changes);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse changes JSON", e);
            }
        }

        return payload;
    }


    public NotificationEventPayloadEntity toEntity(NotificationPayload payload) {

        String changesStr = null;
        if (payload.getChanges() != null && !payload.getChanges().isEmpty()) {
            try {
                changesStr = objectMapper.writeValueAsString(payload.getChanges());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize changes to JSON", e);
            }
        }

        return new NotificationEventPayloadEntity(
                payload.getMessageId(),
                payload.getEventType(),
                payload.getEventId(),
                payload.getOccurredAt(),
                payload.getChangeById(),
                payload.getOwnerId(),
                payload.getEventName(),
                changesStr
        );
    }


    public Notification toDomain(NotificationEntity entity) {

        Notification notification = new Notification();
        notification.setId(entity.getId());
        notification.setUserId(entity.getUserId());
        notification.setPayload(toPayload(entity.getPayload()));
        notification.setRead(entity.getIsRead());
        notification.setCreatedAt(entity.getCreatedAt());
        notification.setReadAt(entity.getReadAt());

        return notification;
    }


    public NotificationEntity toEntity(Notification notification) {

        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.getId());
        entity.setUserId(notification.getUserId());
        entity.setPayload(toEntity(notification.getPayload()));
        entity.setIsRead(notification.getRead());
        entity.setCreatedAt(notification.getCreatedAt());
        entity.setReadAt(notification.getReadAt());

        return entity;
    }



    private String generateMessage(Notification notification) {

        NotificationPayload payload = notification.getPayload();

        switch (payload.getEventType()) {
            case "EVENT_UPDATED":
                return String.format("%s было обновлено", payload.getEventName());
            case "EVENT_CREATED":
                return String.format("Создано новое мероприятие: %s", payload.getEventName());
            case "EVENT_CANCELLED":
                return String.format("%s было отменено", payload.getEventName());
            case "EVENT_STATUS_CHANGED":
                return String.format("Статус мероприятия %s изменен", payload.getEventName());
            default:
                return String.format("Изменения в мероприятии %s", payload.getEventName());
        }
    }
}