package com.example.dormitory.service;

import com.example.dormitory.dto.adminReporter.*;

import java.util.List;
import java.util.UUID;

public interface AdminReporterService {
    List<AdminReporterResponse> getAllReporters();
    AdminReporterResponse getReporterById(UUID reporterId);
    AdminReporterResponse updateReporter(UUID reporterId, AdminReporterUpdateRequest request);
}