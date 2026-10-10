package com.example.dormitory.controller.web;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
public class AdminWorkflowController {

    private final RepairRequestService repairRequestService;
    private final RepairAssignmentService repairAssignmentService;

    @Autowired
    public AdminWorkflowController(RepairRequestService repairRequestService,
                                   RepairAssignmentService repairAssignmentService) {
        this.repairRequestService = repairRequestService;
        this.repairAssignmentService = repairAssignmentService;
    }

    // แท็บ "มอบหมายงาน" — คำร้องที่อนุมัติแล้ว รอมอบหมายช่าง
    @GetMapping("/admin/assignments")
    public String listAssignments(Model model) {
        List<RepairRequest> requests = repairRequestService.getAllRequests().stream()
                .filter(r -> r.getStatus() == RepairRequestStatus.APPROVED)
                .toList();

        model.addAttribute("requests", requests);
        return "admin/assignments-list";
    }

    // แท็บ "ตรวจงาน" — คำร้องที่กำลังดำเนินการ และที่ดำเนินการไม่สำเร็จ
    // ลำดับ: รอตรวจสอบ -> กำลังดำเนินการ -> ไม่สำเร็จ
    @GetMapping("/admin/inspections")
    public String listInspections(Model model) {
        List<RepairRequest> inspectable = repairRequestService.getAllRequests().stream()
                .filter(r -> r.getStatus() == RepairRequestStatus.IN_PROGRESS
                          || r.getStatus() == RepairRequestStatus.IN_COMPLETED)
                .toList();

        // คำร้องที่ช่างอัปเดตสถานะงานเป็น COMPLETED แล้ว (รอแอดมินยืนยัน)
        Set<UUID> readyIds = new HashSet<>();
        // คำร้องที่ดำเนินการไม่สำเร็จ
        Set<UUID> failedIds = new HashSet<>();

        for (RepairRequest r : inspectable) {
            if (r.getStatus() == RepairRequestStatus.IN_COMPLETED) {
                failedIds.add(r.getRepairRequestId());
                continue;
            }
            RepairAssignment assignment =
                    repairAssignmentService.getAssignmentByRequestId(r.getRepairRequestId());
            if (assignment != null
                    && assignment.getJobStatus() != null
                    && "COMPLETED".equals(assignment.getJobStatus().name())) {
                readyIds.add(r.getRepairRequestId());
            }
        }

        List<RepairRequest> requests = inspectable.stream()
                .sorted(Comparator.comparingInt((RepairRequest r) ->
                        readyIds.contains(r.getRepairRequestId()) ? 0
                      : failedIds.contains(r.getRepairRequestId()) ? 2 : 1))
                .toList();

        model.addAttribute("requests", requests);
        model.addAttribute("readyIds", readyIds);
        model.addAttribute("failedIds", failedIds);
        model.addAttribute("readyCount", readyIds.size());
        return "admin/inspections-list";
    }
}