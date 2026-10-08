package com.example.dormitory.service.impl;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.request.AdminReporterUpdateRequest;
import com.example.dormitory.dto.response.AdminReporterResponse;
import com.example.dormitory.dto.response.RepairRequestHistoryResponse;
import com.example.dormitory.repository.AdminReporterRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.service.AdminReporterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminReporterServiceImpl implements AdminReporterService {

    private final AdminReporterRepository reporterRepository;
    private final RepairRequestRepository repairRequestRepository;

    public AdminReporterServiceImpl(AdminReporterRepository reporterRepository,
                                     RepairRequestRepository repairRequestRepository) {
        this.reporterRepository = reporterRepository;
        this.repairRequestRepository = repairRequestRepository;
    }

    @Override
    public List<AdminReporterResponse> getAllReporters() {
        return reporterRepository.findAll().stream()
                .filter(reporter -> reporter.getResident() != null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminReporterResponse getReporterById(UUID reporterId) {
        Reporter reporter = reporterRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้แจ้ง"));

        if (reporter.getResident() == null) {
            throw new RuntimeException("ผู้แจ้งนี้ยังไม่มีข้อมูลผู้พักอาศัยผูกอยู่");
        }

        return toResponse(reporter);
    }

    @Override
    @Transactional
    public AdminReporterResponse updateReporter(UUID reporterId, AdminReporterUpdateRequest request) {
        Reporter reporter = reporterRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้แจ้ง"));

        Resident resident = reporter.getResident();
        if (resident == null) {
            throw new RuntimeException("ผู้แจ้งนี้ยังไม่มีข้อมูลผู้พักอาศัยผูกอยู่ ไม่สามารถแก้ไขได้");
        }

        if (request.getFirstName() != null) resident.setFirstName(request.getFirstName());
        if (request.getLastName() != null) resident.setLastName(request.getLastName());
        if (request.getPhoneNo() != null) resident.setPhoneNo(request.getPhoneNo());

        reporterRepository.save(reporter);
        return toResponse(reporter);
    }

    @Override
    public List<RepairRequestHistoryResponse> getRepairHistoryByReporterId(UUID reporterId) {
        // เช็คก่อนว่า reporter นี้มีอยู่จริง (และ throw error เดียวกับ method อื่นถ้าไม่เจอ)
        reporterRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้แจ้ง"));

        List<RepairRequest> requests =
                repairRequestRepository.findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId);

        return requests.stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());
    }

    private RepairRequestHistoryResponse toHistoryResponse(RepairRequest req) {
        return new RepairRequestHistoryResponse(
                req.getRepairRequestId(),
                req.getRepairType() != null ? req.getRepairType().name() : null,
                req.getStatus() != null ? req.getStatus().name() : null,
                req.getDescription(),
                req.getCreatedAt(),
                req.getStartDateTime(),
                req.getEndDateTime()
        );
    }

    private AdminReporterResponse toResponse(Reporter reporter) {
        User user = reporter.getUser();
        Resident resident = reporter.getResident();

        return new AdminReporterResponse(
                reporter.getReporterId(),
                user != null ? user.getUserId() : null,
                resident != null ? resident.getFirstName() : null,
                resident != null ? resident.getLastName() : null,
                resident != null ? resident.getPhoneNo() : null,
                user != null ? user.getUsername() : null,
                user != null ? user.getEmail() : null,
                resident != null && resident.getRoom() != null ? resident.getRoom().getRoomNo() : null
        );
    }
}