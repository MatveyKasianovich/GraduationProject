package dev.sorokin.eventmanager;


import dev.sorokin.eventmanager.event.EventEntity;
import dev.sorokin.eventmanager.event.EventRepository;
import dev.sorokin.eventmanager.event.EventStatus;
import dev.sorokin.eventmanager.location.LocationEntity;
import dev.sorokin.eventmanager.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты: поднимается реальный PostgreSQL в Docker-контейнере
 * (Testcontainers), Liquibase накатывает миграции проекта
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//Чтобы поднималась именно БД из тестового контейнера, а не H2
class EventRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Long persistOwner() {
        UserEntity owner = new UserEntity(null, "owner-" + System.nanoTime(), "pass", 25, "USER");
        return entityManager.persistAndFlush(owner).getId();
    }

    private Long persistLocation(int capacity) {
        LocationEntity location = new LocationEntity(null, "Hall", "Some address", capacity, "desc");
        return entityManager.persistAndFlush(location).getId();
    }

    private EventEntity buildEvent(String name, Integer cost, String status, Long locationId, Long ownerId) {
        EventEntity event = new EventEntity(name, LocalDateTime.now().plusDays(1), 60, 30, 0, status);
        event.setCost(cost);
        event.setLocationId(locationId);
        event.setOwnerId(ownerId);
        return event;
    }

    @Test
    void saveAndFindById_persistsEvent() {

        Long ownerId = persistOwner();
        Long locationId = persistLocation(100);

        EventEntity toSave = buildEvent("Conference", 150, EventStatus.WAIT_START.name(), locationId, ownerId);
        EventEntity saved = eventRepository.saveAndFlush(toSave);
        entityManager.clear();// Для того чтобы сущность не доставалась из PersContext, а чтобы доставалась из БД(очищаем PersContext)

        Optional<EventEntity> found = eventRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Conference");
        assertThat(found.get().getLocationId()).isEqualTo(locationId);
        assertThat(found.get().getOwnerId()).isEqualTo(ownerId);
        assertThat(eventRepository.existsByLocationId(locationId)).isTrue();
    }

    @Test
    void searchAllByFilter_appliesStatusAndCostFilters() {

        Long ownerId = persistOwner();
        Long locationId = persistLocation(100);

        eventRepository.saveAndFlush(buildEvent("Cheap and active", 100, EventStatus.WAIT_START.name(), locationId, ownerId));
        eventRepository.saveAndFlush(buildEvent("Cancelled", 150, EventStatus.CANCELLED.name(), locationId, ownerId));
        eventRepository.saveAndFlush(buildEvent("Expensive and active", 500, EventStatus.WAIT_START.name(), locationId, ownerId));
        entityManager.clear();

        List<EventEntity> result = eventRepository.searchAllByFilter(
                null, null, null, null, null,
                0, 200,
                null, null,
                null,
                EventStatus.WAIT_START.name(),
                Pageable.ofSize(10)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cheap and active");
    }
}

