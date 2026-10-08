package com.example.dormitory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class RepairRequestHistoryResponse {
    private UUID repairRequestId;
    private String repairType;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public RepairRequestHistoryResponse() {
    }

    public RepairRequestHistoryResponse(UUID repairRequestId, String repairType, String status,
                                         String description, LocalDateTime createdAt,
                                         LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.repairRequestId = repairRequestId;
        this.repairType = repairType;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public UUID getRepairRequestId() { return repairRequestId; }
    public void setRepairRequestId(UUID repairRequestId) { this.repairRequestId = repairRequestId; }

    public String getRepairType() { return repairType; }
    public void setRepairType(String repairType) { this.repairType = repairType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
}