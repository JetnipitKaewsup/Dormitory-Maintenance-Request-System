package com.example.dormitory.controller.web;

import com.example.dormitory.dto.request.AdminResidentCreateRequest;
import com.example.dormitory.dto.request.AdminResidentUpdateRequest;
import com.example.dormitory.service.AdminResidentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/residents")
public class AdminResidentWebController {

    private final AdminResidentService adminResidentService;

    public AdminResidentWebController(AdminResidentService adminResidentService) {
        this.adminResidentService = adminResidentService;
    }

    @GetMapping
    public String listResidentsPage(Model model) {
        model.addAttribute("residents", adminResidentService.getAllResidents());
        model.addAttribute("rooms", adminResidentService.getAllRooms());
        return "admin/residentManage";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createRequest", new AdminResidentCreateRequest());
        model.addAttribute("rooms", adminResidentService.getAllRooms());
        return "admin/residentCreate";
    }


    @PostMapping("/create")
    public String createResidentPage(@Valid @ModelAttribute("createRequest") AdminResidentCreateRequest request,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", adminResidentService.getAllRooms());
            return "admin/residentCreate";
        }
        try {
            adminResidentService.createResident(request);
            redirectAttributes.addFlashAttribute("success", "เพิ่มผู้พักอาศัยสำเร็จ");
            return "redirect:/admin/residents";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("rooms", adminResidentService.getAllRooms());
            return "admin/residentCreate";
        }
    }

    @PostMapping("/update")
    public String updateResidentPage(@RequestParam UUID residentId,
                                      @Valid @ModelAttribute AdminResidentUpdateRequest request,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง");
            return "redirect:/admin/residents";
        }
        try {
            adminResidentService.updateResident(residentId, request);
            redirectAttributes.addFlashAttribute("success", "บันทึกข้อมูลผู้พักอาศัยสำเร็จ");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/residents";
    }
}