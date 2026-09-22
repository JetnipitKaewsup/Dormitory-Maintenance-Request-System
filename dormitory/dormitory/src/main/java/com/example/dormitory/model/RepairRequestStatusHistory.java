package com.example.dormitory.model;


import com.example.dormitory.model.RepairRequest;
import com.example.dormitory.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "RepairRequestStatusHistory")
public class RepairRequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID requestHistoryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repairRequestId", nullable = false)
    private RepairRequest repairRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "changeBy", nullable = false)
    private User changeBy;

    @Column(nullable = false)
    private String newStatus;

    private String previousStatus;

    @Column(nullable = false)
    private LocalDateTime changeDate;

    public RepairRequestStatusHistory() {
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

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }
}