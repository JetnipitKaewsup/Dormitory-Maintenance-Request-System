package com.example.dormitory.controller.api;

import com.example.dormitory.exception.ResourceNotFoundException;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.UpdateReporterDto;
import com.example.dormitory.dto.response.RepairRequestResponse;
import com.example.dormitory.dto.response.ReporterResponse;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reporters")
@Tag(name = "Reporters", description = "จัดการข้อมูลผู้แจ้งซ่อม")
public class ReporterRestController {

    private final ReporterProfileService reporterProfileService;
    private final RepairRequestService repairRequestService;

    public ReporterRestController(ReporterProfileService reporterProfileService,
            RepairRequestService repairRequestService) {
        this.reporterProfileService = reporterProfileService;
        this.repairRequestService = repairRequestService;
    }

    // ==================== GET PROFILE ====================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('REPORTER')")
    @Operation(summary = "ดึงข้อมูล Reporter ตาม ID")
    public ResponseEntity<ReporterResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {

        UUID currentUserId = user.getUserId();
        Reporter reporter = reporterProfileService.getReporterByUserId(currentUserId);

        if (!reporter.getReporterId().equals(id)) {
            throw new ResourceNotFoundException("ไม่พบข้อมูล Reporter id: " + id);
        }
        return ResponseEntity.ok(ReporterResponse.from(reporter)); // 200
    }

    // ==================== UPDATE (PUT) ====================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('REPORTER')")
    @Operation(summary = "อัปเดตข้อมูล Reporter")
    public ResponseEntity<ReporterResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReporterDto dto,
            @AuthenticationPrincipal User user) {

        UUID currentUserId = user.getUserId();
        Reporter reporter = reporterProfileService.getReporterByUserId(currentUserId);

        if (!reporter.getReporterId().equals(id)) {
            throw new ResourceNotFoundException("ไม่พบข้อมูล Reporter id: " + id);
        }

        reporterProfileService.updatePhone(currentUserId, dto.getPhoneNo());
        Reporter updated = reporterProfileService.getReporterByUserId(currentUserId);

        return ResponseEntity.ok(ReporterResponse.from(updated)); // 200
    }

    // GET /api/v1/reporters/{id}/repair-requests → ดูคำร้องของ reporter คนนี้
    @GetMapping("/{id}/repair-requests")
    @PreAuthorize("hasRole('REPORTER')")
    @Operation(summary = "ดึงคำร้องของ Reporter (sub-resource)")
    public ResponseEntity<Page<RepairRequestResponse>> listRequestsByReporter(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @AuthenticationPrincipal User user) {

        UUID currentUserId = user.getUserId();
        Reporter reporter = reporterProfileService.getReporterByUserId(currentUserId);

        if (!reporter.getReporterId().equals(id)) {
            throw new ResourceNotFoundException("ไม่พบข้อมูล Reporter id: " + id);
        }

        Pageable pageable = buildPageable(page, size, sort);
        Page<RepairRequest> result = repairRequestService.getMyRequests(currentUserId, pageable);

        return ResponseEntity.ok(result.map(RepairRequestResponse::from));

    }

    private Pageable buildPageable(int page, int size, String[] sort) {
        String field = (sort != null && sort.length > 0) ? sort[0] : "createdAt";
        Sort.Direction dir = (sort != null && sort.length > 1 && sort[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(dir, field));
    }

    private UUID getUserId(HttpSession session) {
        Object v = session.getAttribute("userId");
        if (v == null)
            throw new ResourceNotFoundException("กรุณาเข้าสู่ระบบใหม่");
        return UUID.fromString(v.toString());
    }
}