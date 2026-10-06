package com.example.dormitory.domain.entity;

import jakarta.persistence.*;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;

@Entity
@Table(name = "repair_assignment")
public class RepairAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assignmentId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "repair_request_id", nullable = false)
    private RepairRequest repairRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "technician_id", nullable = false)
    private Technician technician;

    @ManyToOne(optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_status")
    private RepairRequestStatus jobStatus;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "technician_note")
    private String technicianNote;

    @Column(nullable = false)
    private LocalDateTime assignDate;



    public RepairAssignment() {
    }

    public RepairAssignment(UUID assignmentId,
                            RepairRequest repairRequest,
                            Technician technician,
                            Admin admin,
                            RepairRequestStatus jobStatus,
                            LocalDateTime assignDate,
                            String adminNote,
                            String technicianNote) {
        this.assignmentId = assignmentId;
        this.repairRequest = repairRequest;
        this.technician = technician;
        this.admin = admin;
        this.jobStatus = jobStatus;
        this.adminNote = adminNote;
        this.technicianNote = technicianNote;
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

    public RepairRequestStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(RepairRequestStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public String getTechnicianNote() {
        return technicianNote;
    }

    public void setTechnicianNote(String technicianNote) {
        this.technicianNote = technicianNote;
    }

    public LocalDateTime getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDateTime assignDate) {
        this.assignDate = assignDate;
    }

}