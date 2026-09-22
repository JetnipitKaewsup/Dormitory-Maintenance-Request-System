package com.example.dormitory.controller;

import com.example.dormitory.model.RepairRequest;
import com.example.dormitory.service.RepairRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/requests")
public class AdminRepairRequestController {

    private final RepairRequestService repairRequestService;

    @Autowired
    public AdminRepairRequestController(RepairRequestService repairRequestService) {
        this.repairRequestService = repairRequestService;
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable UUID id, Model model) {
        RepairRequest request = repairRequestService.getById(id);
        model.addAttribute("request", request);
        return "admin/repair-request-detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable UUID id, HttpSession session) {
        UUID adminId = getCurrentAdminId(session);
        repairRequestService.approve(id, adminId);
        return "redirect:/admin/requests/" + id;   // Post-Redirect-Get กัน refresh แล้ว submit ซ้ำ
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable UUID id,
                          @RequestParam(required = false) String reason,
                          HttpSession session) {
        UUID adminId = getCurrentAdminId(session);
        repairRequestService.reject(id, adminId, reason);
        return "redirect:/admin/requests/" + id;
    }

    private UUID getCurrentAdminId(HttpSession session) {
        Object adminId = session.getAttribute("adminId");
        if (adminId == null) {
            throw new IllegalStateException("ยังไม่ได้ login เป็น admin");
        }
        return (UUID) adminId;
    }
}