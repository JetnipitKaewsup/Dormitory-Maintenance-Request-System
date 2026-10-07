package com.example.dormitory.event;

import java.util.UUID;

public record RepairAssignmentCreatedEvent(
        UUID assignmentId,
        UUID repairRequestId,
        UUID technicianUserId,
        UUID adminId
) {
}