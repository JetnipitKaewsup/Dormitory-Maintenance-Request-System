package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.RepairAssignmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RepairAssignmentStatusHistoryRepository
        extends JpaRepository<RepairAssignmentStatusHistory, UUID> {

    List<RepairAssignmentStatusHistory>
    findByAssignment_AssignmentIdOrderByChangeDateDesc(
            UUID assignmentId
    );
}