package dev.sorokin.eventnotificator.entities;

import dev.sorokin.eventnotificator.entities.NotificationEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("""
        SELECT n FROM NotificationEntity n
        JOIN FETCH n.payload p
        WHERE n.userId = :userId
        AND n.isRead = false
        """)
    List<NotificationEntity> findAllUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("""
    UPDATE NotificationEntity n 
    SET n.isRead = true , n.readAt = CURRENT_TIMESTAMP 
    WHERE n.userId = :userId AND n.id IN :ids
    """)
    void markNotificationsAsRead(@Param("userId") Long userId, @Param("ids") List<Long> ids);


    @Query("SELECT COUNT(n) FROM NotificationEntity n " +
            "WHERE n.isRead = false AND n.userId = :userId")
    Long countUnreadByUserId(@Param("userId") Long userId);

}
