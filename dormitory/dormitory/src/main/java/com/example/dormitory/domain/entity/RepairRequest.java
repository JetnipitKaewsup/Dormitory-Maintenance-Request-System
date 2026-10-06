package com.example.dormitory.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.enums.RepairType;

@Entity
@Table(name = "repair_request",
    indexes = {
        @Index(
            name = "idx_repair_request_reporter",
            columnList = "reporter_id"
        ),
        @Index(
            name = "idx_repair_request_admin",
            columnList = "admin_id"
        ),
        @Index(
            name = "idx_repair_request_room",
            columnList = "room_no"
        ),
        @Index(
            name = "idx_repair_request_status",
            columnList = "status"
        ),
        @Index(
            name = "idx_repair_request_created_at",
            columnList = "created_at"
        )
    }

)
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID repairRequestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Reporter reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_no", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairType repairType;

    @Enumerated (EnumType.STRING)
    @Column(nullable = false)
    private RepairRequestStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String reporterNote;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(
        mappedBy = "repairRequest",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<RepairRequestStatusHistory> statusHistories = new ArrayList<>();
    
    public RepairRequest() {
    }

    public RepairRequest(UUID repairRequestId, Reporter reporter, Admin admin,
                        Room room, RepairType repairType, RepairRequestStatus status,
                        String description, String reporterNote,
                        LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.repairRequestId = repairRequestId;
        this.reporter = reporter;
        this.admin = admin;
        this.room = room;
        this.repairType = repairType;
        this.status = status;
        this.description = description;
        this.reporterNote = reporterNote;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.createdAt = LocalDateTime.now();
    }
    // Getter / Setter

    public UUID getRepairRequestId() {
        return repairRequestId;
    }

    public void setRepairRequestId(UUID repairRequestId) {
        this.repairRequestId = repairRequestId;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    public RepairRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RepairRequestStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporterNote() {
        return reporterNote;
    }

    public void setReporterNote(String reporterNote) {
        this.reporterNote = reporterNote;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getCreatedAt() {
    return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    }

    public List<RepairRequestStatusHistory> getStatusHistories() {
        return statusHistories;
    }

    public void setStatusHistories(
            List<RepairRequestStatusHistory> statusHistories) {
        this.statusHistories = statusHistories;
    }

    public void addStatusHistory(
            RepairRequestStatusHistory history) {

        statusHistories.add(history);
        history.setRepairRequest(this);
    }
    
    public void removeStatusHistory(
        RepairRequestStatusHistory history) {

    statusHistories.remove(history);
    history.setRepairRequest(null);
    }
}
