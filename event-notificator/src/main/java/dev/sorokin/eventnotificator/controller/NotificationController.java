package dev.sorokin.eventnotificator.controller;


import dev.sorokin.eventnotificator.dto.MarkNotificationAsReadRequest;
import dev.sorokin.eventnotificator.dto.MarkNotificationAsReadToBusinnes;
import dev.sorokin.eventnotificator.mapper.NotificationMapper;
import dev.sorokin.eventnotificator.notification.NotificationService;
import dev.sorokin.eventnotificator.notification.NotificationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;

    public NotificationController(NotificationMapper notificationMapper, NotificationService notificationService) {
        this.notificationMapper = notificationMapper;
        this.notificationService = notificationService;
    }

    @GetMapping()
    public ResponseEntity<List<NotificationResponse>> getAllUnreadNotifications(){
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getAllNotifications().stream()
                .map(notification ->notificationMapper.toResponse(notification))
                .toList());
    }

    @PostMapping()
    public ResponseEntity<Void> readAllUnreadNotifications(@RequestBody @Valid MarkNotificationAsReadRequest notifications){
        notificationService.markNotificationsAsRead(new MarkNotificationAsReadToBusinnes(notifications.getNotificationIds()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
