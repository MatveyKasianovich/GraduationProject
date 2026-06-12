package dev.sorokin.eventmanager.event;

import dev.sorokin.eventmanager.registration.RegistrationEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "max_places", nullable = false)
    private Integer maxPlaces;

    @Column(name = "occupied_places")
    private Integer occupiedPlaces;

    @Column(name="status",nullable = false)
    private String status;

    @Column(name="cost")
    private Integer cost;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "owner_id")
    private Long ownerId;

    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrations = new ArrayList<>();

    public EventEntity() {}

    public EventEntity(String name, LocalDateTime startAt, Integer durationMinutes,
                       Integer maxPlaces, Integer occupiedPlaces, String status) {
        this.name = name;
        this.startAt = startAt;
        this.durationMinutes = durationMinutes;
        this.maxPlaces = maxPlaces;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
    }

    public EventEntity(Long id, String name, LocalDateTime startAt, Integer durationMinutes, Integer maxPlaces, Integer occupiedPlaces, String status, Integer cost, Long locationId, Long ownerId, List<RegistrationEntity> registrations) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.durationMinutes = durationMinutes;
        this.maxPlaces = maxPlaces;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
        this.cost = cost;
        this.locationId = locationId;
        this.ownerId = ownerId;
        this.registrations = registrations;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getMaxPlaces() { return maxPlaces; }
    public void setMaxPlaces(Integer maxPlaces) { this.maxPlaces = maxPlaces; }

    public Integer getOccupiedPlaces() { return occupiedPlaces; }
    public void setOccupiedPlaces(Integer occupiedPlaces) { this.occupiedPlaces = occupiedPlaces; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public List<RegistrationEntity> getRegistrations() { return registrations; }
    public void setRegistrations(List<RegistrationEntity> registrations) { this.registrations = registrations; }
}