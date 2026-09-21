package com.example.dormitory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_assignment_status_history")
public class RepairAssignmentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID historyId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private RepairAssignment assignment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "change_by", nullable = false)
    private User changeBy;

    @Column(nullable = false)
    private String newStatus;

    @Column(nullable = false)
    private LocalDateTime changeDate;

    private String previousStatus;

    public RepairAssignmentStatusHistory() {
    }

    // Getter / Setter

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }

    public RepairAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(RepairAssignment assignment) {
        this.assignment = assignment;
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

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }
}