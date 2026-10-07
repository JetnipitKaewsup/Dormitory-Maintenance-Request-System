package com.example.dormitory.service;

import com.example.dormitory.dto.request.AdminTechnicianCreateRequest;
import com.example.dormitory.dto.request.AdminTechnicianUpdateRequest;
import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.dto.response.AdminTechnicianHistoryResponse;

import java.util.List;
import java.util.UUID;

public interface AdminTechnicianService {
    List<AdminTechnicianResponse> getAllTechnicians();
    AdminTechnicianResponse getTechnicianById(UUID technicianId);
    AdminTechnicianResponse updateTechnician(UUID technicianId, AdminTechnicianUpdateRequest request);
    AdminTechnicianResponse createTechnician(AdminTechnicianCreateRequest request);
    List<AdminTechnicianHistoryResponse> getRepairHistoryByTechnicianId(UUID technicianId);
}