package com.example.dormitory.controller.web;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminWorkflowController {

    private final RepairRequestService repairRequestService;

    @Autowired
    public AdminWorkflowController(RepairRequestService repairRequestService) {
        this.repairRequestService = repairRequestService;
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

    // แท็บ "ตรวจงาน" — คำร้องที่กำลังดำเนินการ รอตรวจสอบ/ยืนยัน
    @GetMapping("/admin/inspections")
    public String listInspections(Model model) {
        List<RepairRequest> requests = repairRequestService.getAllRequests().stream()
                .filter(r -> r.getStatus() == RepairRequestStatus.IN_PROGRESS)
                .toList();

        model.addAttribute("requests", requests);
        return "admin/inspections-list";
    }
}