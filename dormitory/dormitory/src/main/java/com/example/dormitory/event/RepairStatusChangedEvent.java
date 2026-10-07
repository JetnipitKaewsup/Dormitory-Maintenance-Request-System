package com.example.dormitory.event;

import java.util.UUID;

import com.example.dormitory.domain.enums.RepairRequestStatus;

public record RepairStatusChangedEvent(

        UUID repairRequestId,

        UUID assignmentId,

        RepairRequestStatus previousStatus,

        RepairRequestStatus newStatus,

        UUID changedBy,

        String changedByRole,

        String note

) {
}
