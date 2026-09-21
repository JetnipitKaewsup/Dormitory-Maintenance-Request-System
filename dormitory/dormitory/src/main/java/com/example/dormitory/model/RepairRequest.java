package com.example.dormitory.model;

import com.example.dormitory.model.Reporter;
import com.example.dormitory.model.Admin;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "RepairRequest")
public class RepairRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID repairRequestId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporterId", nullable = false)
    private Reporter reporter;

    @ManyToOne
    @JoinColumn(name = "adminId")
    private Admin admin;


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

    public RepairRequest(UUID repairRequestId, Reporter reporter, Admin admin,
                        String repairType, String status,
                        String description, String remark,
                        LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.repairRequestId = repairRequestId;
        this.reporter = reporter;
        this.admin = admin;
        this.repairType = repairType;
        this.status = status;
        this.description = description;
        this.remark = remark;
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
