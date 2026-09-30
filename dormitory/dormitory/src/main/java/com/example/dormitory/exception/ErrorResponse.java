package com.example.dormitory.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        int status,
        LocalDateTime timestamp,
        List<String> details
) {
    public ErrorResponse(String code, String message, int status) {
        this(code, message, status, LocalDateTime.now(), null);
    }
}