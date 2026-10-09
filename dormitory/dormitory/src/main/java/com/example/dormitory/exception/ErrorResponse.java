package com.example.dormitory.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.example.dormitory.util.DateTimeUtil;

public record ErrorResponse(
        String code,
        String message,
        int status,
        LocalDateTime timestamp,
        List<String> details
) {
    public ErrorResponse(String code, String message, int status) {
        this(code, message, status, DateTimeUtil.now(), null);
    }
}