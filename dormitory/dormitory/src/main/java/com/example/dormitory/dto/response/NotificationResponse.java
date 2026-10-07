package com.example.dormitory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID notificationId,
        String type,
        String title,
        String message,
        UUID referenceId,
        boolean read,
        LocalDateTime createdAt
) {
}
