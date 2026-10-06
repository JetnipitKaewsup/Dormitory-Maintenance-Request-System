package com.example.dormitory.service;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.RepairRequestStatusHistory;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.dto.RepairRequestForm;

import java.util.*;

import org.springframework.data.domain.*;

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

    Optional<RepairRequest> getLatestRequest(UUID userId);

    List<RepairRequestStatusHistory> getRequestHistory(
            UUID userId,
            UUID repairRequestId);

    void cancelRequest(
            UUID userId,
            UUID repairRequestId);

    Page<RepairRequest> getMyRequests(UUID userId,Pageable pageable);
    //Page<RepairRequest> getMyRequestsByStatus(UUID userId, RepairRequestStatus status, Pageable pageable);
    void updateStatus(UUID userId, UUID requestId, RepairRequestStatus newStatus, String note);

        void adminUpdateStatus(UUID repairRequestId, UUID adminId, RepairRequestStatus newStatus, String note);    
    // Admin - list all
    List<RepairRequest> getAllRequests();
}