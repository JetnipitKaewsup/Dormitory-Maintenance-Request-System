package com.example.dormitory.controller.web;

import com.example.dormitory.dto.request.AdminReporterUpdateRequest;
import com.example.dormitory.service.AdminReporterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/reporters")
public class AdminReporterWebController {

    private final AdminReporterService adminReporterService;

    public AdminReporterWebController(AdminReporterService adminReporterService) {
        this.adminReporterService = adminReporterService;
    }

    @GetMapping
    public String listReportersPage(Model model) {
        model.addAttribute("reporters", adminReporterService.getAllReporters());
        return "admin/reporterManage";
    }

    @PostMapping("/update")
    public String updateReporterPage(@RequestParam UUID reporterId,
                                      @Valid @ModelAttribute AdminReporterUpdateRequest request,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง");
            return "redirect:/admin/reporters";
        }
        try {
            adminReporterService.updateReporter(reporterId, request);
            redirectAttributes.addFlashAttribute("success", "บันทึกข้อมูลผู้แจ้งสำเร็จ");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reporters";
    }

    // ดูประวัติคำร้องซ่อมของ reporter คนนี้
    @GetMapping("/{reporterId}/history")
    public String viewRepairHistory(@PathVariable UUID reporterId, Model model) {
        model.addAttribute("reporter", adminReporterService.getReporterById(reporterId));
        model.addAttribute("history", adminReporterService.getRepairHistoryByReporterId(reporterId));
        return "admin/reporterHistory";
    }
}