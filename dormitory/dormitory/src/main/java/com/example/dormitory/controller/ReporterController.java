
package com.example.dormitory.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.dormitory.dto.ProfileForm;
import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.model.RepairRequest;
import com.example.dormitory.model.Reporter;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;

import jakarta.servlet.http.HttpSession;

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

        Reporter reporter =
                reporterProfileService.getReporterByUserId(userId);

        model.addAttribute("repairForm", new RepairRequestForm());
        model.addAttribute("reporter", reporter);

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
            repairRequestService.createRequest(userId, form);

            return "redirect:/reporter/requests";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("error", e.getMessage());

            Reporter reporter =
                    reporterProfileService.getReporterByUserId(userId);

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

        Reporter reporter =
                reporterProfileService.getReporterByUserId(userId);

        model.addAttribute("reporter", reporter);

        return "reporter/ReporterRequests";
    }

    // รายละเอียดคำร้อง
    @GetMapping("/requests/{id}")
    public String showRequestDetail(
            @PathVariable UUID id,
            HttpSession session,
            Model model) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        RepairRequest request =
                repairRequestService.getMyRequest(userId, id);

        model.addAttribute("request", request);

        model.addAttribute(
                "history",
                repairRequestService.getRequestHistory(userId, id));

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

        RepairRequest request =
                repairRequestService.getLatestRequest(userId);

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

        repairRequestService.cancelRequest(userId, id);

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

        repairRequestService.confirmCompletion(userId, id);

        return "redirect:/reporter/requests/" + id;
    }

    // แสดงหน้าแก้ไขโปรไฟล์
    @GetMapping("/profile/edit")
    public String showEditProfile(
            HttpSession session,
            Model model) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        Reporter reporter =
                reporterProfileService.getReporterByUserId(userId);

        ProfileForm profileForm = new ProfileForm();
        profileForm.setPhoneNo(reporter.getUser().getPhoneNo());

        model.addAttribute("reporter", reporter);
        model.addAttribute("profileForm", profileForm);

        return "reporter/ReporterEditProfile";
    }

    // บันทึกการแก้ไขเบอร์โทรศัพท์
    @PostMapping("/profile/edit")
    public String updateProfile(
            @ModelAttribute("profileForm") ProfileForm form,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        UUID userId = getUserId(session);

        if (userId == null) {
            return "redirect:/login";
        }

        Reporter reporter =
                reporterProfileService.getReporterByUserId(userId);

        String phoneNo = form.getPhoneNo();

        // ตรวจสอบเบอร์โทรศัพท์ฝั่ง Backend
        if (phoneNo == null || !phoneNo.matches("[0-9]{10}")) {

            model.addAttribute("reporter", reporter);
            model.addAttribute("profileForm", form);
            model.addAttribute(
                    "errorMessage",
                    "กรุณากรอกเบอร์โทรศัพท์เป็นตัวเลข 10 หลัก");

            return "reporter/ReporterEditProfile";
        }

        try {
            reporterProfileService.updatePhone(userId, phoneNo);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "แก้ไขเบอร์โทรศัพท์สำเร็จ");

            return "redirect:/reporter/profile/edit";

        } catch (IllegalArgumentException | IllegalStateException e) {

            model.addAttribute("reporter", reporter);
            model.addAttribute("profileForm", form);
            model.addAttribute("errorMessage", e.getMessage());

            return "reporter/ReporterEditProfile";
        }
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
}