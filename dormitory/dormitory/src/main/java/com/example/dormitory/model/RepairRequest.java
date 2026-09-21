package com.example.dormitory.model;

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
    private String remark;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public RepairRequest() {
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}
