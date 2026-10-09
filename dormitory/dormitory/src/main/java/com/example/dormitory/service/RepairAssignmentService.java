package com.example.dormitory.service;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.dto.response.DailyRepairJobDto;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;

import java.util.List;
import java.util.UUID;

public interface RepairAssignmentService {

    // งานประจำวันของช่าง
    List<DailyRepairJobDto> getDailyJobs(UUID technicianId);

    List<DailyRepairJobDto> getDailyJobs(
            UUID technicianId,
            RepairJobFilter filter
    );

    // สรุปงานประจำวัน
    DailyRepairSummaryDto getDailySummary(UUID technicianId);

    // ค้นหาข้อมูล Assignment
    RepairAssignment getAssignmentByRequestId(UUID repairRequestId);

    // รายชื่อช่าง
    List<Technician> getAllTechnicians();

    // ช่างรายงานผลการดำเนินงาน
    void updateJobStatus(
            UUID assignmentId,
            UUID technicianId,
            RepairRequestStatus newStatus,
            String technicianNote
    );

    // Admin มอบหมายงานให้ช่าง
    RepairAssignment assignTechnician(
            UUID repairRequestId,
            UUID technicianId,
            UUID adminId,
            String adminNote
    );
}