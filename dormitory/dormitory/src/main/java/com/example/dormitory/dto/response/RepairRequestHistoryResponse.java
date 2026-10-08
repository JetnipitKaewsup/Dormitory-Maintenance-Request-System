package com.example.dormitory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class RepairRequestHistoryResponse {
    private UUID repairRequestId;
    private String repairType;
    private String status;
    private String description;
    private LocalDateTime createdAt;       // วันที่แจ้ง
    private LocalDateTime assignDate;      // วันที่ admin มอบหมาย (จาก RepairAssignment)
    private LocalDateTime completeDate;    // วันที่ tech กดเสร็จ (จาก StatusHistory)

    public RepairRequestHistoryResponse() {
    }

    public RepairRequestHistoryResponse(UUID repairRequestId, String repairType,
                                         String status, String description,
                                         LocalDateTime createdAt,
                                         LocalDateTime assignDate,
                                         LocalDateTime completeDate) {
        this.repairRequestId = repairRequestId;
        this.repairType = repairType;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.assignDate = assignDate;
        this.completeDate = completeDate;
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

    public LocalDateTime getAssignDate() { return assignDate; }
    public void setAssignDate(LocalDateTime assignDate) { this.assignDate = assignDate; }

    public LocalDateTime getCompleteDate() { return completeDate; }
    public void setCompleteDate(LocalDateTime completeDate) { this.completeDate = completeDate; }
}