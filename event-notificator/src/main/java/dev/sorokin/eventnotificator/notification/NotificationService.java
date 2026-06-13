package dev.sorokin.eventnotificator.notification;

import dev.sorokin.eventnotificator.dto.MarkNotificationAsReadToBusinnes;
import dev.sorokin.eventnotificator.entities.NotificationRepository;
import dev.sorokin.eventnotificator.jwt.SecurityUtils;
import dev.sorokin.eventnotificator.mapper.NotificationMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationMapper notificationMapper, NotificationRepository notificationRepository) {
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllUnreadByUserId(SecurityUtils.getCurrentUserId()).stream()
                .map(notificationEntity -> notificationMapper.toDomain(notificationEntity))
                .toList();

    }

    @Transactional
    public void markNotificationsAsRead(MarkNotificationAsReadToBusinnes notifications) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationRepository.markNotificationsAsRead(currentUserId, notifications.getNotificationIds());
    }
}
