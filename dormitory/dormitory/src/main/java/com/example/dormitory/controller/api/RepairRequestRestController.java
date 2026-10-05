package com.example.dormitory.controller.api;
import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.dto.response.RepairRequestResponse;
import com.example.dormitory.exception.ResourceNotFoundException;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.dto.request.CreateRepairRequestDto;
import com.example.dormitory.dto.UpdateStatusDto;
import com.example.dormitory.service.RepairRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/v1/repair-requests")
@Tag(name = "Repair Requests", description = "จัดการคำร้องแจ้งซ่อม")
public class RepairRequestRestController {

    private final RepairRequestService repairRequestService;

    public RepairRequestRestController(RepairRequestService repairRequestService) {
        this.repairRequestService = repairRequestService;
    }

    // ==================== LIST ====================
    @GetMapping
    @PreAuthorize("hasRole('REPORTER')")
    @Operation(summary = "ดึงรายการคำร้องทั้งหมด (paginated)")
    public ResponseEntity<Page<RepairRequestResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User user) {

        UUID userId = user.getUserId();
        Pageable pageable = buildPageable(page, size, sort);

       
        Page<RepairRequest> result =
                repairRequestService.getMyRequests(userId, pageable);

        return ResponseEntity.ok(result.map(RepairRequestResponse::from));
    }

    // ==================== GET by ID ====================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('REPORTER')")
    public ResponseEntity<RepairRequestResponse> getById(
            @PathVariable UUID id, 
            @AuthenticationPrincipal User user) {
        UUID userId = user.getUserId();
        RepairRequest request = repairRequestService.getMyRequest(userId, id);
        return ResponseEntity.ok(RepairRequestResponse.from(request));
    }

    // ==================== CREATE ====================
    @PostMapping
    @PreAuthorize("hasRole('REPORTER')")
    public ResponseEntity<RepairRequestResponse> create(
            @Valid @RequestBody CreateRepairRequestDto dto,
            @AuthenticationPrincipal User user) {
        UUID userId = user.getUserId();
        RepairRequestForm form = mapToForm(dto);
        RepairRequest saved = repairRequestService.createRequest(userId, form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RepairRequestResponse.from(saved));
    }

    // ==================== UPDATE STATUS ====================
    @PatchMapping("/{id}/status")
    public ResponseEntity<RepairRequestResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusDto dto,
            @AuthenticationPrincipal User user) {

        UUID userId = user.getUserId();

        repairRequestService.updateStatus(userId, id, dto.getStatus(), dto.getNote());
        RepairRequest updated = repairRequestService.getMyRequest(userId, id);
        return ResponseEntity.ok(RepairRequestResponse.from(updated));
    }

    // ==================== CANCEL ====================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('REPORTER')")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID id, 
            @AuthenticationPrincipal User user) {
        UUID userId = user.getUserId();
        repairRequestService.cancelRequest(userId, id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Helpers ====================
    private UUID getUserId(HttpSession session) {
        Object v = session.getAttribute("userId");
        if (v == null) throw new ResourceNotFoundException("กรุณาเข้าสู่ระบบใหม่");
        return UUID.fromString(v.toString());
    }

    private Pageable buildPageable(int page, int size, String[] sort) {
        String field = sort.length > 0 ? sort[0] : "createdAt";
        Sort.Direction dir = sort.length > 1 && sort[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(dir, field));
    }

    private RepairRequestForm mapToForm(CreateRepairRequestDto dto) {
        RepairRequestForm form = new RepairRequestForm();
        form.setRepairType(dto.getRepairType());
        form.setDescription(dto.getDescription());
        form.setPreferredDate(dto.getPreferredDate());
        form.setStartTime(dto.getStartTime());
        form.setEndTime(dto.getEndTime());
        form.setReporterNote(dto.getReporterNote());
        return form;
    }
}