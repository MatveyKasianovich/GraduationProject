package dev.sorokin.eventmanager.dto;

import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        String detailedMessage,
        LocalDateTime localDateTime
) {
}
