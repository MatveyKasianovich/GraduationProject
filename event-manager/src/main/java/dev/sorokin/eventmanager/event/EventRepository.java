package dev.sorokin.eventmanager.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity,Long> {
    Optional<EventEntity> findById(Long id);

    boolean existsByLocationId(Long id);

    @Query("""
    SELECT e FROM EventEntity e 
    WHERE e.locationId = :locationId 
    AND e.maxPlaces > :capacity
    """)
    List<EventEntity> findAllByLocationIdWithMaxPlaces(
            @Param("locationId") Long locationId,
            @Param("capacity") int capacity
    );

    @Query("SELECT COUNT(r) FROM RegistrationEntity r WHERE r.event.id = :eventId")
    int countRegistrationsByEventId(@Param("eventId") Long eventId);

    @Query("""
        SELECT DISTINCT e FROM EventEntity e 
        LEFT JOIN FETCH e.registrations 
        WHERE e.ownerId = :ownerId
        """)
    List<EventEntity> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("""
    SELECT DISTINCT e FROM EventEntity e
    LEFT JOIN FETCH e.registrations r
    WHERE (e.name = COALESCE(:name, e.name))
    AND (e.maxPlaces >= COALESCE(:placeMin, e.maxPlaces))
    AND (e.maxPlaces <= COALESCE(:placeMax, e.maxPlaces))
    AND (e.startAt >= COALESCE(:dateStartAfter, e.startAt))
    AND (e.startAt <= COALESCE(:dateStartBefore, e.startAt))
    AND (e.cost >= COALESCE(:costMin, e.cost))
    AND (e.cost <= COALESCE(:costMax, e.cost))
    AND (e.durationMinutes >= COALESCE(:durationMin, e.durationMinutes))
    AND (e.durationMinutes <= COALESCE(:durationMax, e.durationMinutes))
    AND (e.locationId = COALESCE(:locationId, e.locationId))
    AND (e.status = COALESCE(:status, e.status))
    ORDER BY e.id ASC 
""")
    List<EventEntity> searchAllByFilter(
            @Param("name") String name,
            @Param("placeMin") Integer placeMin,
            @Param("placeMax") Integer placeMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Long locationId,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT e FROM EventEntity e
            JOIN FETCH e.registrations r
            WHERE r.user.id = :currentUserId
            """)
    List<EventEntity> findEventsByUserRegistration(@Param("currentUserId") Long currentUserId);


    @Query("SELECT e FROM EventEntity e WHERE e.status = :status AND e.startAt <= :now")
    List<EventEntity> findAllByStatusAndStartAtBefore(
            @Param("status") String status,
            @Param("now") LocalDateTime now
    );


    @Query("SELECT e FROM EventEntity e WHERE e.status = :status")
    List<EventEntity> findAllByStatus(@Param("status") String status);

}
