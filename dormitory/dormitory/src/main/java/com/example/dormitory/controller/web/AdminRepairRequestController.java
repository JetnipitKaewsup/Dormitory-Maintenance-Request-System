package com.example.dormitory.controller.web;

import com.example.dormitory.domain.command.RepairCommand;
import com.example.dormitory.domain.command.impl.AssignTechnicianCommand;
import com.example.dormitory.domain.command.impl.ConfirmCompletionCommand;
import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/requests")
public class AdminRepairRequestController {

    private final RepairRequestService repairRequestService;
    private final RepairAssignmentService repairAssignmentService;

    @Autowired
    public AdminRepairRequestController(RepairRequestService repairRequestService,
                                         RepairAssignmentService repairAssignmentService) {
        this.repairRequestService = repairRequestService;
        this.repairAssignmentService = repairAssignmentService;
    }

    @GetMapping
    public String listRequests(Model model) {
        List<RepairRequest> requests = repairRequestService.getAllRequests();

        long pendingCount = requests.stream().filter(r -> r.getStatus() == RepairRequestStatus.PENDING).count();
        long approvedCount = requests.stream().filter(r -> r.getStatus() == RepairRequestStatus.APPROVED).count();
        long inProgressCount = requests.stream().filter(r -> r.getStatus() == RepairRequestStatus.IN_PROGRESS).count();
        long completedCount = requests.stream().filter(r -> r.getStatus() == RepairRequestStatus.COMPLETED).count();
        long rejectedCount = requests.stream().filter(r ->
                r.getStatus() == RepairRequestStatus.REJECTED).count();

        model.addAttribute("requests", requests);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "admin/requests-list";
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable UUID id, Model model) {
        RepairRequest request = repairRequestService.getById(id);
        model.addAttribute("request", request);
        return "admin/repair-request-detail";
    }

    @ExceptionHandler(IllegalStateException.class)
public String handleNotLoggedIn(IllegalStateException ex, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("error", "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่");
    return "redirect:/login";
    }
    
    @PostMapping("/{id}/approve")
    public String approve(@PathVariable UUID id, HttpSession session) {
        UUID adminId = getCurrentAdminId(session);
        repairRequestService.approve(id, adminId);
        return "redirect:/admin/requests/" + id;
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable UUID id,
                          @RequestParam(required = false) String reason,
                          HttpSession session) {
        UUID adminId = getCurrentAdminId(session);
        repairRequestService.reject(id, adminId, reason);
        return "redirect:/admin/requests/" + id;
    }

    // ===================== มอบหมายงานให้ช่าง =====================

    @GetMapping("/{id}/assign")
    public String showAssignPage(@PathVariable UUID id, Model model) {
        RepairRequest request = repairRequestService.getById(id);
        List<Technician> technicians = repairAssignmentService.getAllTechnicians();

        model.addAttribute("request", request);
        model.addAttribute("technicians", technicians);
        return "admin/assign-technician";
    }

    @PostMapping("/{id}/assign")
    public String submitAssign(@PathVariable UUID id,
                                @RequestParam UUID technicianId,
                                @RequestParam(required = false) String adminNote,
                                HttpSession session) {
        UUID adminId = getCurrentAdminId(session);

        RepairCommand command = new AssignTechnicianCommand(
                repairAssignmentService, id, technicianId, adminId, adminNote);
        command.execute();

        return "redirect:/admin/requests/" + id;
    }

    // ===================== ตรวจสอบงาน =====================

    @GetMapping("/{id}/inspect")
    public String showInspectPage(@PathVariable UUID id, Model model) {
        RepairRequest request = repairRequestService.getById(id);
        RepairAssignment assignment = repairAssignmentService.getAssignmentByRequestId(id);

        model.addAttribute("request", request);
        model.addAttribute("assignment", assignment);
        return "admin/inspect-work";
    }

    @PostMapping("/{id}/inspect")
    public String confirmCompletion(@PathVariable UUID id,
                                     @RequestParam(required = false) String note,
                                     HttpSession session) {
        UUID adminId = getCurrentAdminId(session);

        RepairCommand command = new ConfirmCompletionCommand(
                repairRequestService, id, adminId, note);
        command.execute();

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