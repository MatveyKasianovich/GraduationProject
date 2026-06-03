package dev.sorokin.eventmanager.entityToBusinnes;

import java.time.LocalDateTime;

public class Event {

    private Long id;
    private String name;
    private LocalDateTime startAt;
    private Integer durationMinutes;
    private Integer maxPlaces;
    private Long locationId;
    private Integer occupiedPlaces;
    private String status;
    private Integer cost;
    private Long ownerId;

    // Конструктор для создания нового события (без id)
    public Event(String name, LocalDateTime startAt, Integer durationMinutes,
                 Integer maxPlaces, Long locationId, Integer occupiedPlaces,
                 String status, Integer cost, Long ownerId) {
        this.name = name;
        this.startAt = startAt;
        this.durationMinutes = durationMinutes;
        this.maxPlaces = maxPlaces;
        this.locationId = locationId;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
        this.cost = cost;
        this.ownerId = ownerId;
    }

    // Полный конструктор (для существующего события с id)
    public Event(Long id, String name, LocalDateTime startAt, Integer durationMinutes,
                 Integer maxPlaces, Long locationId, Integer occupiedPlaces,
                 String status, Integer cost, Long ownerId) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.durationMinutes = durationMinutes;
        this.maxPlaces = maxPlaces;
        this.locationId = locationId;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
        this.cost = cost;
        this.ownerId = ownerId;
    }

    // Геттеры и сеттеры
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

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public Integer getOccupiedPlaces() { return occupiedPlaces; }
    public void setOccupiedPlaces(Integer occupiedPlaces) { this.occupiedPlaces = occupiedPlaces; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}