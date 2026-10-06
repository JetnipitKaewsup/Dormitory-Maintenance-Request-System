package com.example.dormitory.service;

import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.dto.request.AdminResidentCreateRequest;
import com.example.dormitory.dto.request.AdminResidentUpdateRequest;
import com.example.dormitory.dto.response.AdminResidentResponse;

import java.util.List;
import java.util.UUID;

public interface AdminResidentService {
    List<AdminResidentResponse> getAllResidents();
    AdminResidentResponse getResidentById(UUID residentId);
    AdminResidentResponse createResident(AdminResidentCreateRequest request);
    AdminResidentResponse updateResident(UUID residentId, AdminResidentUpdateRequest request);
    List<Room> getAllRooms();
}