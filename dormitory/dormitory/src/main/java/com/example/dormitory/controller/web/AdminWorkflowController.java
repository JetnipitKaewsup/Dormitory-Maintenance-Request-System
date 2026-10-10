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

    // แท็บ "ตรวจงาน" — เฉพาะคำร้องที่ยังเป็น IN_PROGRESS
    // ลำดับ: รอตรวจสอบ (ช่างแจ้งเสร็จ/ไม่สำเร็จ) -> กำลังดำเนินการ
    // คำร้องที่ปิดแล้ว (COMPLETED / IN_COMPLETED) ไม่แสดงในหน้านี้
    @GetMapping("/admin/inspections")
    public String listInspections(Model model) {
        List<RepairRequest> inProgress = repairRequestService.getAllRequests().stream()
                .filter(r -> r.getStatus() == RepairRequestStatus.IN_PROGRESS)
                .toList();

        // ช่างแจ้งงานเสร็จ (COMPLETED) รอแอดมินยืนยัน
        Set<UUID> readyIds = new HashSet<>();
        // ช่างแจ้งดำเนินงานไม่สำเร็จ (IN_COMPLETED) รอแอดมินตรวจสอบ
        Set<UUID> techFailedIds = new HashSet<>();

        for (RepairRequest r : inProgress) {
            RepairAssignment assignment =
                    repairAssignmentService.getAssignmentByRequestId(r.getRepairRequestId());
            if (assignment == null || assignment.getJobStatus() == null) {
                continue;
            }

            String job = assignment.getJobStatus().name();
            if ("COMPLETED".equals(job)) {
                readyIds.add(r.getRepairRequestId());
            } else if ("IN_COMPLETED".equals(job)) {
                techFailedIds.add(r.getRepairRequestId());
            }
        }

        // งานที่รอตรวจสอบขึ้นก่อน ที่เหลือคงลำดับเดิม
        List<RepairRequest> requests = inProgress.stream()
                .sorted(Comparator.comparing((RepairRequest r) ->
                        !(readyIds.contains(r.getRepairRequestId())
                          || techFailedIds.contains(r.getRepairRequestId()))))
                .toList();

        model.addAttribute("requests", requests);
        model.addAttribute("readyIds", readyIds);
        model.addAttribute("techFailedIds", techFailedIds);
        model.addAttribute("readyCount", readyIds.size() + techFailedIds.size());
        return "admin/inspections-list";
    }
}