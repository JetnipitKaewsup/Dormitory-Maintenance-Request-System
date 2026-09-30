package com.example.dormitory.repository;


import com.example.dormitory.domain.enums.RepairRequestStatus;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dormitory.domain.entity.RepairRequest;

import java.util.*;

public interface RepairRequestRepository extends JpaRepository<RepairRequest, UUID> {
    // ดึงคำร้องทั้งหมดของ Reporter คนนี้
    List<RepairRequest> findByReporter_ReporterIdOrderByStartDateTimeDesc(
            UUID reporterId);

    // ดึงคำร้องเฉพาะของ user ที่ login อยู่
    Optional<RepairRequest> findByRepairRequestIdAndReporter_User_UserId(
            UUID repairRequestId,
            UUID userId);

    // ดึงคำร้องล่าสุดของ Reporter
    Optional<RepairRequest> findFirstByReporter_ReporterIdOrderByCreatedAtDesc(
            UUID reporterId);

    // สำหรับ pagination
    Page<RepairRequest> findByReporter_ReporterId(UUID reporterId, Pageable pageable);

    // สำหรับ REST แบบ filter by status
    Page<RepairRequest> findByReporter_ReporterIdAndStatus(
            UUID reporterId, RepairRequestStatus status, Pageable pageable);
}
//