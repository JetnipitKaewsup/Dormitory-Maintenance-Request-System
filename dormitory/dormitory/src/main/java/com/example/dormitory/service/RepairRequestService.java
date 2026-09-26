package com.example.dormitory.service;

import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.model.RepairRequest;
import com.example.dormitory.model.RepairRequestStatusHistory;

import java.util.*;

public interface RepairRequestService {

    RepairRequest getById(UUID id);
    // Admin
    void approve(UUID repairRequestId, UUID adminId);

    void reject(UUID repairRequestId, UUID adminId, String rejectReason);

    // Reporter
    RepairRequest createRequest(
            UUID userId,
            RepairRequestForm form);

    List<RepairRequest> getMyRequests(UUID userId);

    RepairRequest getMyRequest(
            UUID userId,
            UUID repairRequestId);

    RepairRequest getLatestRequest(UUID userId);

    List<RepairRequestStatusHistory> getRequestHistory(
            UUID userId,
            UUID repairRequestId);

    void cancelRequest(
            UUID userId,
            UUID repairRequestId);

    void confirmCompletion(
            UUID userId,
            UUID repairRequestId);
}