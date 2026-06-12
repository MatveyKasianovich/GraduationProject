package dev.sorokin.eventcommon.kafka;

import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        String detailedMessage,
        LocalDateTime localDateTime
) {
}
