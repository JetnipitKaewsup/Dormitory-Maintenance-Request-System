package com.example.dormitory.domain.entity;

import jakarta.persistence.*;

import com.example.dormitory.domain.enums.RepairRequestStatus;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.User;

@Entity
@Table(name = "RepairAssignmentStatusHistory")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private RepairRequestStatus newStatus;

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private RepairRequestStatus previousStatus;

        public RepairAssignmentStatusHistory() {
    }

    public RepairAssignmentStatusHistory(UUID historyId,
                                        RepairAssignment assignment,
                                        User changeBy,
                                        RepairRequestStatus newStatus,
                                        LocalDateTime changeDate,
                                        RepairRequestStatus previousStatus) {
        this.historyId = historyId;
        this.assignment = assignment;
        this.changeBy = changeBy;
        this.newStatus = newStatus;
        this.changeDate = changeDate;
        this.previousStatus = previousStatus;
    }


    public UUID getHistoryId() {
        return historyId;
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

    public RepairRequestStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(RepairRequestStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public RepairRequestStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(RepairRequestStatus previousStatus) {
        this.previousStatus = previousStatus;
    }
}