package dev.sorokin.eventnotificator.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.sorokin.eventcommon.kafka.NotificationPayload;
import dev.sorokin.eventnotificator.notification.NotificationCreationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationKafkaListener.class);

    private final NotificationCreationService notificationCreationService;

    public NotificationKafkaListener(NotificationCreationService notificationCreationService) {
        this.notificationCreationService = notificationCreationService;
    }

    @KafkaListener(
            topics = "notifications-topic",
            containerFactory = "containerFactory",
            groupId = "notificator-group"
    )
    public void listenEventUpdates(ConsumerRecord<String, NotificationPayload> record) throws JsonProcessingException {

        NotificationPayload payload = record.value();

        log.info("Received event update from Kafka. MessageId={}, EventType={}",
                payload.getMessageId(),
                payload.getEventType());

        notificationCreationService.processEventUpdate(payload);

    }
}