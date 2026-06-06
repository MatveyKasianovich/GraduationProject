package dev.sorokin.eventmanager.registration;

import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity,Long> {


    @Query("SELECT r FROM RegistrationEntity r " +
            "JOIN FETCH r.user " +
            "JOIN FETCH r.event " +
            "WHERE r.user.id = :currentUserId AND r.event.id = :eventId")
    Optional<RegistrationEntity> findCurrentUserRegistrationOnEvent(
            @Param("currentUserId") Long currentUserId,
            @Param("eventId") Long eventId
    );



}
