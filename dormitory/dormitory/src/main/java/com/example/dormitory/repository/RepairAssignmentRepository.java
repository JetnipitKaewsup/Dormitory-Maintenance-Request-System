package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.RepairAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.dormitory.domain.enums.RepairRequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RepairAssignmentRepository
        extends JpaRepository<RepairAssignment, UUID> {

    List<RepairAssignment> findByTechnician_TechnicianIdAndAssignDateBetween(
            UUID technicianId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<RepairAssignment> findByTechnician_TechnicianIdAndAssignDateBetweenAndJobStatus(
            UUID technicianId,
            LocalDateTime start,
            LocalDateTime end,
            RepairRequestStatus jobStatus
    );

    List<RepairAssignment> findByTechnician_TechnicianIdAndAssignDateBetweenAndJobStatusNot(
            UUID technicianId,
            LocalDateTime start,
            LocalDateTime end,
            RepairRequestStatus jobStatus
    );
}