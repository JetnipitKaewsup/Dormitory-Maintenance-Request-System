package com.example.dormitory.repository;

import com.example.dormitory.model.RepairRequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RepairRequestStatusHistoryRepository extends JpaRepository<RepairRequestStatusHistory, UUID> {

    // ดึงประวัติสถานะของคำร้อง เรียงจากเก่าไปใหม่
    List<RepairRequestStatusHistory> findByRepairRequest_RepairRequestIdOrderByChangeDateAsc(
            UUID repairRequestId);
}