package com.example.dormitory.service;

import com.example.dormitory.dto.adminReporter.*;
import com.example.dormitory.model.Reporter;
import com.example.dormitory.model.Resident;
import com.example.dormitory.model.User;
import com.example.dormitory.repository.AdminReporterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminReporterServiceImpl implements AdminReporterService {

    private final AdminReporterRepository reporterRepository;

    public AdminReporterServiceImpl(AdminReporterRepository reporterRepository) {
        this.reporterRepository = reporterRepository;
    }

    @Override
    public List<AdminReporterResponse> getAllReporters() {
        return reporterRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminReporterResponse getReporterById(UUID reporterId) {
        Reporter reporter = reporterRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้แจ้ง"));
        return toResponse(reporter);
    }

    @Override
    @Transactional
    public AdminReporterResponse updateReporter(UUID reporterId, AdminReporterUpdateRequest request) {
        Reporter reporter = reporterRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้แจ้ง"));

        Resident resident = reporter.getResident();
        if (resident != null) {
            if (request.getFirstName() != null) resident.setFirstName(request.getFirstName());
            if (request.getLastName() != null) resident.setLastName(request.getLastName());
            if (request.getPhoneNo() != null) resident.setPhoneNo(request.getPhoneNo());
        }

        reporterRepository.save(reporter);
        return toResponse(reporter);
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