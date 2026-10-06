package com.example.dormitory.controller.api;

import com.example.dormitory.dto.request.AdminResidentCreateRequest;
import com.example.dormitory.dto.request.AdminResidentUpdateRequest;
import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.service.AdminResidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/residents")
public class AdminResidentApiController {

    private final AdminResidentService adminResidentService;

    public AdminResidentApiController(AdminResidentService adminResidentService) {
        this.adminResidentService = adminResidentService;
    }

    @GetMapping
    public ResponseEntity<List<AdminResidentResponse>> getAllResidents() {
        return ResponseEntity.ok(adminResidentService.getAllResidents());
    }

    @GetMapping("/{residentId}")
    public ResponseEntity<AdminResidentResponse> getResident(@PathVariable UUID residentId) {
        return ResponseEntity.ok(adminResidentService.getResidentById(residentId));
    }

    @PostMapping
    public ResponseEntity<AdminResidentResponse> createResident(
            @RequestBody AdminResidentCreateRequest request) {
        AdminResidentResponse created = adminResidentService.createResident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{residentId}")
    public ResponseEntity<AdminResidentResponse> updateResident(
            @PathVariable UUID residentId,
            @RequestBody AdminResidentUpdateRequest request) {
        return ResponseEntity.ok(adminResidentService.updateResident(residentId, request));
    }
}