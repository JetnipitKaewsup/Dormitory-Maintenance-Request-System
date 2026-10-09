package com.example.dormitory.service;

import com.example.dormitory.dto.request.AdminReporterUpdateRequest;
import com.example.dormitory.dto.response.AdminReporterResponse;
import com.example.dormitory.dto.response.RepairRequestHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface AdminReporterService {
    List<AdminReporterResponse> getAllReporters();
    AdminReporterResponse getReporterById(UUID reporterId);
    AdminReporterResponse updateReporter(UUID reporterId, AdminReporterUpdateRequest request);
    List<RepairRequestHistoryResponse> getRepairHistoryByReporterId(UUID reporterId);
}