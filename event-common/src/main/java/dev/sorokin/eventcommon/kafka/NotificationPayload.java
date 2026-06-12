package dev.sorokin.eventcommon.kafka;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationPayload {

    private String messageId;
    private String eventType;
    private Long eventId;
    private LocalDateTime occurredAt;
    private Long changeById;
    private Long ownerId;
    private String eventName;
    private List<Long>usersId;
    private List<NotificationChange> changes;

    public NotificationPayload(String messageId, String eventType, Long eventId, LocalDateTime occurredAt, Long changeById, Long ownerId, String eventName, List<Long> usersId, List<NotificationChange> changes) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.changeById = changeById;
        this.ownerId = ownerId;
        this.eventName = eventName;
        this.usersId = usersId;
        this.changes = changes;
    }

    public NotificationPayload(String messageId, String eventType, Long eventId, LocalDateTime occurredAt, Long ownerId, String eventName, List<Long> usersId, List<NotificationChange> changes) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.ownerId = ownerId;
        this.eventName = eventName;
        this.usersId = usersId;
        this.changes = changes;
    }

    public String getMessageId() {
        return messageId;
    }

    public NotificationPayload() {
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getChangeById() {
        return changeById;
    }

    public void setChangeById(Long changeById) {
        this.changeById = changeById;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<Long> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<Long> usersId) {
        this.usersId = usersId;
    }

    public List<NotificationChange> getChanges() {
        return changes;
    }

    public void setChanges(List<NotificationChange> changes) {
        this.changes = changes;
    }
}
