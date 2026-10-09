package com.example.dormitory.controller.web;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.dto.response.DailyRepairJobDto;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;
import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.TechnicianProfileService;

@Controller
@RequestMapping("/technician")
public class RepairWorkController {

    private final RepairAssignmentService repairAssignmentService;
    private final TechnicianProfileService technicianProfileService;

    public RepairWorkController(
            RepairAssignmentService repairAssignmentService,
            TechnicianProfileService technicianProfileService) {

        this.repairAssignmentService = repairAssignmentService;
        this.technicianProfileService = technicianProfileService;
    }

    // งานซ่อมประจำวัน
    @GetMapping("/dailywork")
    public String viewDailyJobs(
            @AuthenticationPrincipal User user,
            @RequestParam(
                    name = "filter",
                    defaultValue = "ALL"
            ) RepairJobFilter filter,
            Model model) {

        UUID userId = user.getUserId();

        if (userId == null) {
            return "redirect:/login";
        }

        Technician technician =
                technicianProfileService
                        .getTechnicianByUserId(userId);

        UUID technicianId =
                technician.getTechnicianId();

        List<DailyRepairJobDto> jobs =
                repairAssignmentService
                        .getDailyJobs(technicianId, filter);

        DailyRepairSummaryDto summary =
                repairAssignmentService
                        .getDailySummary(technicianId);

        model.addAttribute("jobs", jobs);
        model.addAttribute("technician", technician);
        model.addAttribute("technicianId", technicianId);
        model.addAttribute("filter", filter);
        model.addAttribute("summary", summary);

        return "technician/dailywork";
    }

    // เปลี่ยนสถานะงานซ่อม
    @PostMapping("/repair/{assignmentId}/status")
    public String updateJobStatus(
            @PathVariable UUID assignmentId,
            @RequestParam RepairRequestStatus status,
            @RequestParam(required = false) String technicianNote,
            @AuthenticationPrincipal User user) {

        UUID userId = user.getUserId();

        if (userId == null) {
            return "redirect:/login";
        }

        Technician technician =
                technicianProfileService
                        .getTechnicianByUserId(userId);

        repairAssignmentService.updateJobStatus(
                assignmentId,
                technician.getTechnicianId(),
                status,
                technicianNote
        );

        return "redirect:/technician/dailywork";
    }

    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal User user,
            Model model) {

        UUID userId = user.getUserId();

        if (userId == null) {
            return "redirect:/login";
        }

        Technician technician =
                technicianProfileService
                        .getTechnicianByUserId(userId);

        model.addAttribute("technician", technician);

        return "technician/profile";
    }
}