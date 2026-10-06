package com.example.dormitory.controller.api;

import com.example.dormitory.dto.adminReporter.*;
import com.example.dormitory.service.AdminReporterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/reporters")
public class AdminReporterApiController {

    private final AdminReporterService adminReporterService;

    public AdminReporterApiController(AdminReporterService adminReporterService) {
        this.adminReporterService = adminReporterService;
    }

    @GetMapping
    public ResponseEntity<List<AdminReporterResponse>> getAllReporters() {
        return ResponseEntity.ok(adminReporterService.getAllReporters());
    }

    @GetMapping("/{reporterId}")
    public ResponseEntity<AdminReporterResponse> getReporter(@PathVariable UUID reporterId) {
        return ResponseEntity.ok(adminReporterService.getReporterById(reporterId));
    }

    @PutMapping("/{reporterId}")
    public ResponseEntity<AdminReporterResponse> updateReporter(
            @PathVariable UUID reporterId,
            @RequestBody AdminReporterUpdateRequest request) {
        return ResponseEntity.ok(adminReporterService.updateReporter(reporterId, request));
    }
}