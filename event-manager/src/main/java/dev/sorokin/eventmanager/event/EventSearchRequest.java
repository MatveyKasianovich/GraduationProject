package dev.sorokin.eventmanager.event;

import java.time.LocalDateTime;

public class EventSearchRequest {
    private String name;
    private Integer placeMin;
    private Integer placeMax;
    private LocalDateTime dateStartAfter;
    private LocalDateTime dateStartBefore;
    private  Integer costMin;
    private Integer costMax;
    private Integer durationMin;
    private Integer durationMax;
    private Long locationId;
    private EventStatus status;

    public EventSearchRequest() {
    }

    public EventSearchRequest(String name, Integer placeMin, Integer placeMax, LocalDateTime dateStartAfter, LocalDateTime dateStartBefore, Integer costMin, Integer costMax, Integer durationMin, Integer durationMax, Long locationId, EventStatus status) {
        this.name = name;
        this.placeMin = placeMin;
        this.placeMax = placeMax;
        this.dateStartAfter = dateStartAfter;
        this.dateStartBefore = dateStartBefore;
        this.costMin = costMin;
        this.costMax = costMax;
        this.durationMin = durationMin;
        this.durationMax = durationMax;
        this.locationId = locationId;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlaceMin() {
        return placeMin;
    }

    public void setPlaceMin(Integer placeMin) {
        this.placeMin = placeMin;
    }

    public Integer getPlaceMax() {
        return placeMax;
    }

    public void setPlaceMax(Integer placeMax) {
        this.placeMax = placeMax;
    }

    public LocalDateTime getDateStartAfter() {
        return dateStartAfter;
    }

    public void setDateStartAfter(LocalDateTime dateStartAfter) {
        this.dateStartAfter = dateStartAfter;
    }

    public LocalDateTime getDateStartBefore() {
        return dateStartBefore;
    }

    public void setDateStartBefore(LocalDateTime dateStartBefore) {
        this.dateStartBefore = dateStartBefore;
    }

    public Integer getCostMin() {
        return costMin;
    }

    public void setCostMin(Integer costMin) {
        this.costMin = costMin;
    }

    public Integer getCostMax() {
        return costMax;
    }

    public void setCostMax(Integer costMax) {
        this.costMax = costMax;
    }

    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Integer durationMin) {
        this.durationMin = durationMin;
    }

    public Integer getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(Integer durationMax) {
        this.durationMax = durationMax;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
