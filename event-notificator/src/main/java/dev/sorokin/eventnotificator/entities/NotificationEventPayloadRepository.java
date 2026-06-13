package dev.sorokin.eventnotificator.entities;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventPayloadRepository extends JpaRepository<NotificationEventPayloadEntity, Long> {
}
