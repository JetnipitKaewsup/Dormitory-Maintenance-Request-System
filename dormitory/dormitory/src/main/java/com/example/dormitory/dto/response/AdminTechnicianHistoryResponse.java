package com.example.dormitory.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminTechnicianHistoryResponse {
    private UUID assignmentId;
    private UUID repairRequestId;
    private String repairType;
    private String description;
    private String statusLabel;
    private String statusCssClass;
    private String adminNote;
    private String technicianNote;
    private LocalDateTime assignDate;

    public AdminTechnicianHistoryResponse() {
    }

    public AdminTechnicianHistoryResponse(UUID assignmentId, UUID repairRequestId, String repairType,
                                           String description, String statusLabel, String statusCssClass,
                                           String adminNote, String technicianNote, LocalDateTime assignDate) {
        this.assignmentId = assignmentId;
        this.repairRequestId = repairRequestId;
        this.repairType = repairType;
        this.description = description;
        this.statusLabel = statusLabel;
        this.statusCssClass = statusCssClass;
        this.adminNote = adminNote;
        this.technicianNote = technicianNote;
        this.assignDate = assignDate;
    }

    public UUID getAssignmentId() { return assignmentId; }
    public void setAssignmentId(UUID assignmentId) { this.assignmentId = assignmentId; }

    public UUID getRepairRequestId() { return repairRequestId; }
    public void setRepairRequestId(UUID repairRequestId) { this.repairRequestId = repairRequestId; }

    public String getRepairType() { return repairType; }
    public void setRepairType(String repairType) { this.repairType = repairType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public String getStatusCssClass() { return statusCssClass; }
    public void setStatusCssClass(String statusCssClass) { this.statusCssClass = statusCssClass; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public String getTechnicianNote() { return technicianNote; }
    public void setTechnicianNote(String technicianNote) { this.technicianNote = technicianNote; }

    public LocalDateTime getAssignDate() { return assignDate; }
    public void setAssignDate(LocalDateTime assignDate) { this.assignDate = assignDate; }
}