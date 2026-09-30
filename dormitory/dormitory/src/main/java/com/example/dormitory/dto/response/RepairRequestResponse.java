package com.example.dormitory.dto.response;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.enums.RepairRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RepairRequestResponse(
        UUID repairRequestId,
        String reporterName,
        int roomNo,
        int buildingNo,
        String repairType,
        String description,
        String reporterNote,
        RepairRequestStatus status,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        LocalDateTime createdAt
) {
    public static RepairRequestResponse from(RepairRequest r) {
        return new RepairRequestResponse(
                r.getRepairRequestId(),
                r.getReporter().getUser().getFirstName() + " " + r.getReporter().getUser().getLastName(),
                r.getRoom().getRoomNo(),
                r.getRoom().getBuilding().getBuildingNo(),
                r.getRepairType() != null ? r.getRepairType().name() : null,
                r.getDescription(),
                r.getReporterNote(),
                r.getStatus(),
                r.getStartDateTime(),
                r.getEndDateTime(),
                r.getCreatedAt()
        );
    }
}