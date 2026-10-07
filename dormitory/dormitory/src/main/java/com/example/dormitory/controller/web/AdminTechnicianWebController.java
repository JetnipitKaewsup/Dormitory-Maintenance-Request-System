package com.example.dormitory.controller.web;

import com.example.dormitory.dto.request.AdminTechnicianCreateRequest;
import com.example.dormitory.dto.request.AdminTechnicianUpdateRequest;
import com.example.dormitory.service.AdminTechnicianService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/technicians")
public class AdminTechnicianWebController {

    private final AdminTechnicianService adminTechnicianService;

    public AdminTechnicianWebController(AdminTechnicianService adminTechnicianService) {
        this.adminTechnicianService = adminTechnicianService;
    }

    @GetMapping
    public String listTechniciansPage(Model model) {
        model.addAttribute("technicians", adminTechnicianService.getAllTechnicians());
        return "admin/technicianManage";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createRequest", new AdminTechnicianCreateRequest());
        return "admin/technicianCreate";
    }

    @PostMapping("/create")
    public String createTechnicianPage(@Valid @ModelAttribute("createRequest") AdminTechnicianCreateRequest request,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/technicianCreate";
        }
        try {
            adminTechnicianService.createTechnician(request);
            redirectAttributes.addFlashAttribute("success", "สร้างบัญชีช่างสำเร็จ");
            return "redirect:/admin/technicians";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/technicianCreate";
        }
    }

    @PostMapping("/update")
    public String updateTechnicianPage(@RequestParam UUID technicianId,
                                        @Valid @ModelAttribute AdminTechnicianUpdateRequest request,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง");
            return "redirect:/admin/technicians";
        }
        try {
            adminTechnicianService.updateTechnician(technicianId, request);
            redirectAttributes.addFlashAttribute("success", "บันทึกข้อมูลช่างสำเร็จ");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/technicians";
    }

    @GetMapping("/{technicianId}/history")
    public String viewRepairHistory(@PathVariable UUID technicianId, Model model) {
            model.addAttribute("technician", adminTechnicianService.getTechnicianById(technicianId));
            model.addAttribute("history", adminTechnicianService.getRepairHistoryByTechnicianId(technicianId));
        return "admin/technicianHistory";
}

}