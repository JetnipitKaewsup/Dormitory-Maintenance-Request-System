package com.example.dormitory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "RepairAssignment")
public class RepairAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assignmentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repairRequestId", nullable = false)
    private RepairRequest repairRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "technicianId", nullable = false)
    private Technician technician;

    @ManyToOne(optional = false)
    @JoinColumn(name = "adminId", nullable = false)
    private Admin admin;

    @Column(nullable = false)
    private String jobStatus;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private LocalDateTime assignDate;

    public RepairAssignment() {
    }

    public RepairAssignment(UUID assignmentId,
                            RepairRequest repairRequest,
                            Technician technician,
                            Admin admin,
                            String jobStatus,
                            String note,
                            LocalDateTime assignDate) {
        this.assignmentId = assignmentId;
        this.repairRequest = repairRequest;
        this.technician = technician;
        this.admin = admin;
        this.jobStatus = jobStatus;
        this.note = note;
        this.assignDate = assignDate;
    }
    // Getter / Setter

    public UUID getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public RepairRequest getRepairRequest() {
        return repairRequest;
    }

    public void setRepairRequest(RepairRequest repairRequest) {
        this.repairRequest = repairRequest;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDateTime assignDate) {
        this.assignDate = assignDate;
    }
}