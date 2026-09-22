package com.example.dormitory.service;

import com.example.dormitory.model.RepairRequest;
import java.util.UUID;

public interface RepairRequestService {

    RepairRequest getById(UUID id);

    void approve(UUID repairRequestId, UUID adminId);

    void reject(UUID repairRequestId, UUID adminId, String rejectReason);
}