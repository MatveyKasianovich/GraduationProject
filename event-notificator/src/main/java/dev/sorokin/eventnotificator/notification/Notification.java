package dev.sorokin.eventnotificator.notification;

import dev.sorokin.eventcommon.kafka.NotificationPayload;
import dev.sorokin.eventnotificator.entities.NotificationEventPayloadEntity;

import java.time.LocalDateTime;

public class Notification {

    private Long id;
    private Long userId;
    private NotificationPayload payload;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Notification(Long id, Long userId, NotificationPayload payload, Boolean isRead, LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.payload = payload;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public Notification() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationPayload getPayload() {
        return payload;
    }

    public void setPayload(NotificationPayload payload) {
        this.payload = payload;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
