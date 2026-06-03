package dev.sorokin.eventmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class EventResponseDTO {

    private Long id;
    private String name;
    private LocalDateTime date;
    private Integer duration;
    private Integer cost;
    private Integer maxPlaces;
    private Integer occupiedPlaces;
    private Long locationId;
    private Long ownerId;
    private String status;


    public EventResponseDTO(Long id, String name, LocalDateTime date, Integer duration,
                            Integer cost, Integer maxPlaces, Integer occupiedPlaces,
                            Long locationId, Long ownerId, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.cost = cost;
        this.maxPlaces = maxPlaces;
        this.occupiedPlaces = occupiedPlaces;
        this.locationId = locationId;
        this.ownerId = ownerId;
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public Integer getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(Integer occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}