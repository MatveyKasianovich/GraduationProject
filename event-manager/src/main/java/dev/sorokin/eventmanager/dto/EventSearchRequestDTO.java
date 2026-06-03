package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.data.EventStatus;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDateTime;

public record EventSearchRequestDTO (
        String name,
        Integer placeMin,
        Integer placeMax,
        LocalDateTime dateStartAfter,
        LocalDateTime dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Integer locationId,
        EventStatus status
){
}
