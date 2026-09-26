package com.example.dormitory.model;

import com.example.dormitory.model.Reporter;
import com.example.dormitory.model.Admin;
import com.example.dormitory.model.Room;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_request")
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID repairRequestId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Reporter reporter;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_no", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String repairType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String reporterNote;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public RepairRequest() {
    }

    public RepairRequest(UUID repairRequestId, Reporter reporter, Admin admin,
                        Room room, String repairType, String status,
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

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}
