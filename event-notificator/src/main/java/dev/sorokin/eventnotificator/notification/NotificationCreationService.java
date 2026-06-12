package dev.sorokin.eventnotificator.notification;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventcommon.kafka.NotificationPayload;
import dev.sorokin.eventnotificator.entities.NotificationEntity;
import dev.sorokin.eventnotificator.entities.NotificationEventPayloadEntity;
import dev.sorokin.eventnotificator.entities.NotificationEventPayloadRepository;
import dev.sorokin.eventnotificator.entities.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationCreationService {

    private final NotificationRepository notificationRepository;
    private final NotificationEventPayloadRepository notificationEventPayloadRepository;
    private final ObjectMapper objectMapper;

    public NotificationCreationService(NotificationRepository notificationRepository, NotificationEventPayloadRepository notificationEventPayloadRepository, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationEventPayloadRepository = notificationEventPayloadRepository;
        this.objectMapper = objectMapper;
    }

    public void processEventUpdate(NotificationPayload payload) throws JsonProcessingException {

        String changesStr = objectMapper.writeValueAsString(payload.getChanges());

        NotificationEventPayloadEntity newEntity=notificationEventPayloadRepository.save(new NotificationEventPayloadEntity(
                payload.getMessageId(),
                payload.getEventType(),
                payload.getEventId(),
                payload.getOccurredAt(),
                payload.getChangeById(),
                payload.getOwnerId(),
                payload.getEventName(),
                changesStr
        ));


        payload.getUsersId().stream().forEach(userId -> notificationRepository.save(new NotificationEntity(
                userId,
                newEntity
        )));
    }
}
