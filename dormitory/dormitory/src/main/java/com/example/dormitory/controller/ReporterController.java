package com.example.dormitory.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.model.RepairRequest;
import com.example.dormitory.model.Reporter;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.ReporterRepository;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;

import jakarta.servlet.http.HttpSession;

/**
 * ReporterController
 */
@Controller
@RequestMapping("/reporter")
public class ReporterController {
    private final RepairRequestService repairRequestService;
    private final ReporterProfileService reporterProfileService;

    public ReporterController(
            RepairRequestService repairRequestService,
            ReporterProfileService reporterProfileService) {

        this.repairRequestService = repairRequestService;
        this.reporterProfileService = reporterProfileService;
    }

    // เพิ่ม Repair Request ใหม่
    @GetMapping("/add")
    public String showAddForm(
            HttpSession session,
            Model model) {
        UUID userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        Reporter reporter = reporterProfileService.getReporterByUserId(userId);

        model.addAttribute(
                "repairForm",
                new RepairRequestForm());

        model.addAttribute(
                "reporter",
                reporter);

        return "reporter/ReporterAddRequest";
    }

    @PostMapping("/add")
    public String createRequest(
            @ModelAttribute("repairForm") RepairRequestForm form,
            HttpSession session,
            Model model) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        try {

            repairRequestService.createRequest(
                    userId,
                    form);

            return "redirect:/reporter/requests";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("error", e.getMessage());

            Reporter reporter = reporterProfileService.getReporterByUserId(userId);

            model.addAttribute("reporter", reporter);

            return "reporter/ReporterAddRequest";
        }
    }

    // ประวัติคำร้อง
    @GetMapping("/requests")
    public String showRequests(
            HttpSession session,
            Model model) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "requests",
                repairRequestService.getMyRequests(userId));

        Reporter reporter = reporterProfileService.getReporterByUserId(userId);

        model.addAttribute("reporter", reporter);

        return "reporter/ReporterRequests";
    }

    @GetMapping("/requests/{id}")
    public String showRequestDetail(
            @PathVariable UUID id,
            HttpSession session,
            Model model) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        RepairRequest request = repairRequestService.getMyRequest(
                userId,
                id);

        model.addAttribute("request", request);

        model.addAttribute(
                "history",
                repairRequestService.getRequestHistory(
                        userId,
                        id));

        return "reporter/ReporterRequestDetail";
    }

    // คำร้องล่าสุด
    @GetMapping("/latest")
    public String showLatestRequest(
            HttpSession session,
            Model model) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        RepairRequest request = repairRequestService.getLatestRequest(userId);

        model.addAttribute("request", request);

        return "reporter/ReporterLatest";
    }

    // ยกเลิกคำร้อง
    @PostMapping("/requests/{id}/cancel")
    public String cancelRequest(
            @PathVariable UUID id,
            HttpSession session) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        repairRequestService.cancelRequest(
                userId,
                id);

        return "redirect:/reporter/requests";
    }

    // ยืนยันงานซ่อมเสร็จ
    @PostMapping("/requests/{id}/confirm-completion")
    public String confirmCompletion(
            @PathVariable UUID id,
            HttpSession session) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        repairRequestService.confirmCompletion(
                userId,
                id);

        return "redirect:/reporter/requests/" + id;
    }

    // อ่าน User ID จาก Session
    private UUID getUserId(HttpSession session) {

        Object value = session.getAttribute("userId");

        if (value == null) {
            return null;
        }

        try {
            return UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // แสดงหน้าแก้ไขข้อมูลผู้ใช้
    @GetMapping("/profile/edit")
    public String showEditProfile(
            HttpSession session,
            Model model) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        Reporter reporter = reporterProfileService.getReporterByUserId(userId);

        model.addAttribute("reporter", reporter);

        return "reporter/ReporterEditProfile";
    }
}