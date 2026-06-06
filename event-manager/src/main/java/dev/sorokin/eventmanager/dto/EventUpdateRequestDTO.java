package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.event.EventStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EventUpdateRequestDTO {

    @Future
    private LocalDateTime startAt;

    @Min(10)
    private Integer durationMinutes;

    @Min(0)
    private Integer cost;

    @Min(5)
    private Integer maxPlaces;

    @NotNull
    private Long locationId;

    @NotBlank
    private String name;




    public EventUpdateRequestDTO(LocalDateTime startAt, Integer durationMinutes, Integer cost, Integer maxPlaces, Long locationId, String name) {
        this.startAt = startAt;
        this.durationMinutes = durationMinutes;
        this.cost=cost;
        this.maxPlaces = maxPlaces;
        this.locationId = locationId;
        this.name = name;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
