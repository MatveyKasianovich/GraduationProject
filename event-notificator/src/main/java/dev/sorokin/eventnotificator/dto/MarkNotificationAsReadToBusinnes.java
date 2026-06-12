package dev.sorokin.eventnotificator.dto;

import java.util.List;

public class MarkNotificationAsReadToBusinnes {
    private List<Long> notificationIds;

    public MarkNotificationAsReadToBusinnes(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }

    public MarkNotificationAsReadToBusinnes() {
    }

    public List<Long> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }
}
