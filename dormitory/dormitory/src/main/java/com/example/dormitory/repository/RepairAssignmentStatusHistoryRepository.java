package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.RepairAssignmentStatusHistory;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepairAssignmentStatusHistoryRepository
        extends JpaRepository<RepairAssignmentStatusHistory, UUID> {

    List<RepairAssignmentStatusHistory>
    findByAssignment_AssignmentIdOrderByChangeDateDesc(UUID assignmentId);

    Optional<RepairAssignmentStatusHistory>
        findFirstByAssignment_AssignmentIdAndNewStatusOrderByChangeDateDesc(
            UUID assignmentId, RepairRequestStatus newStatus);
}