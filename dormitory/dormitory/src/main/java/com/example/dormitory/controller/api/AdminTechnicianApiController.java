package com.example.dormitory.controller.api;

import com.example.dormitory.dto.request.*;
import com.example.dormitory.dto.response.*;
import com.example.dormitory.service.AdminTechnicianService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/technicians")
public class AdminTechnicianApiController {

    private final AdminTechnicianService adminTechnicianService;

    public AdminTechnicianApiController(AdminTechnicianService adminTechnicianService) {
        this.adminTechnicianService = adminTechnicianService;
    }

    @GetMapping
    public ResponseEntity<List<AdminTechnicianResponse>> getAllTechnicians() {
        return ResponseEntity.ok(adminTechnicianService.getAllTechnicians());
    }

    @GetMapping("/{technicianId}")
    public ResponseEntity<AdminTechnicianResponse> getTechnician(@PathVariable UUID technicianId) {
        return ResponseEntity.ok(adminTechnicianService.getTechnicianById(technicianId));
    }

    @PutMapping("/{technicianId}")
    public ResponseEntity<AdminTechnicianResponse> updateTechnician(
            @PathVariable UUID technicianId,
            @RequestBody AdminTechnicianUpdateRequest request) {
        return ResponseEntity.ok(adminTechnicianService.updateTechnician(technicianId, request));
    }

    @PostMapping
    public ResponseEntity<AdminTechnicianResponse> createTechnician(
            @RequestBody AdminTechnicianCreateRequest request) {
        AdminTechnicianResponse created = adminTechnicianService.createTechnician(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}