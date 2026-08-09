package dev.sorokin.eventnotificator.notification;

import dev.sorokin.eventnotificator.cache.CacheService;
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
    private final CacheService cacheService;

    public NotificationService(NotificationMapper notificationMapper, NotificationRepository notificationRepository, CacheService cacheService) {
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
        this.cacheService = cacheService;
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
        Long amountOfUnreadNotifications=notificationRepository.countUnreadByUserId(currentUserId);
        cacheService.decrementCacheValue(currentUserId.toString(),amountOfUnreadNotifications);
    }
}
