package com.example.dormitory.service;

import com.example.dormitory.dto.adminTechnician.*;

import java.util.List;
import java.util.UUID;

public interface AdminTechnicianService {
    List<AdminTechnicianResponse> getAllTechnicians();
    AdminTechnicianResponse getTechnicianById(UUID technicianId);
    AdminTechnicianResponse updateTechnician(UUID technicianId, AdminTechnicianUpdateRequest request);
    AdminTechnicianResponse createTechnician(AdminTechnicianCreateRequest request);
}