package com.example.dormitory.domain.entity;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;

@Entity
@Table(name = "repair_request_status_history")
public class RepairRequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID requestHistoryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repair_request_id", nullable = false)
    private RepairRequest repairRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "change_by", nullable = false)
    private User changeBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairRequestStatus newStatus;

    @Enumerated(EnumType.STRING)
    private RepairRequestStatus previousStatus;

    @Column(nullable = false)
    private LocalDateTime changeDate;

    public RepairRequestStatusHistory() {
    }
    public RepairRequestStatusHistory(RepairRequest repairRequest, User changeBy,
                                       RepairRequestStatus newStatus, RepairRequestStatus previousStatus,
                                       LocalDateTime changeDate) {
        this.repairRequest = repairRequest;
        this.changeBy = changeBy;
        this.newStatus = newStatus;
        this.previousStatus = previousStatus;
        this.changeDate = changeDate;
    }  

    // Getter / Setter

    public UUID getRequestHistoryId() {
        return requestHistoryId;
    }

    public void setRequestHistoryId(UUID requestHistoryId) {
        this.requestHistoryId = requestHistoryId;
    }

    public RepairRequest getRepairRequest() {
        return repairRequest;
    }

    public void setRepairRequest(RepairRequest repairRequest) {
        this.repairRequest = repairRequest;
    }

    public User getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(User changeBy) {
        this.changeBy = changeBy;
    }

    public RepairRequestStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RepairRequestStatus newStatus) {
        this.newStatus = newStatus;
    }

    public RepairRequestStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(RepairRequestStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}