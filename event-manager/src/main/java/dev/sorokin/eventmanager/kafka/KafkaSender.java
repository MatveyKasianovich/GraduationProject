package dev.sorokin.eventmanager.kafka;


import dev.sorokin.eventcommon.kafka.NotificationPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSender {

    private static final Logger log = LoggerFactory.getLogger(KafkaSender.class);

    private final KafkaTemplate<String, NotificationPayload> kafkaTemplate;


    public KafkaSender(KafkaTemplate<String, NotificationPayload> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(NotificationPayload notificationPayload) {

        log.info("Sending notification={},notificationPayload={}",notificationPayload);

        var result=kafkaTemplate.send(
                "notifications-topic",
                notificationPayload.getMessageId(),
                notificationPayload
                );

        result.thenAccept(sendResult->
            log.info("Notification sent={},result={}",notificationPayload,result)
        );

    }
}
