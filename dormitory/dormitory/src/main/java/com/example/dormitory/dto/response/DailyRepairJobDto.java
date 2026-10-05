package com.example.dormitory.dto.response;

import java.util.UUID;

public record DailyRepairJobDto(
        UUID assignmentId,
        String time,
        String description,
        String building,
        String room,
        String phone,
        String adminNote,
        String status,
        String reporterNote
) {
}