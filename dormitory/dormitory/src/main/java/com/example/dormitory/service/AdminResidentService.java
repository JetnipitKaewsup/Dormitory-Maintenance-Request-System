package com.example.dormitory.service;

import com.example.dormitory.dto.adminResident.*;
import com.example.dormitory.model.Room;

import java.util.List;
import java.util.UUID;

public interface AdminResidentService {
    List<AdminResidentResponse> getAllResidents();
    AdminResidentResponse getResidentById(UUID residentId);
    AdminResidentResponse createResident(AdminResidentCreateRequest request);
    AdminResidentResponse updateResident(UUID residentId, AdminResidentUpdateRequest request);
    List<Room> getAllRooms();
}