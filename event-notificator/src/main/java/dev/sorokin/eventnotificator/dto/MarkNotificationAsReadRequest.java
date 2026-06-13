package dev.sorokin.eventnotificator.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class MarkNotificationAsReadRequest {

    @NotEmpty
    private List<Long>notificationIds;

    public MarkNotificationAsReadRequest(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }

    public MarkNotificationAsReadRequest() {
    }

    public List<Long> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }
}
