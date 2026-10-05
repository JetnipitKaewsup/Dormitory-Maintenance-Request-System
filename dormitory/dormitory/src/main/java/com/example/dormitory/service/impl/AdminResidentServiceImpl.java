package com.example.dormitory.service;

import com.example.dormitory.dto.adminResident.*;
import com.example.dormitory.model.Resident;
import com.example.dormitory.model.Room;
import com.example.dormitory.repository.AdminResidentRepository;
import com.example.dormitory.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminResidentServiceImpl implements AdminResidentService {

    private final AdminResidentRepository residentRepository;
    private final RoomRepository roomRepository;

    public AdminResidentServiceImpl(AdminResidentRepository residentRepository,
                                     RoomRepository roomRepository) {
        this.residentRepository = residentRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public List<AdminResidentResponse> getAllResidents() {
        return residentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminResidentResponse getResidentById(UUID residentId) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้พักอาศัย"));
        return toResponse(resident);
    }

    @Override
    @Transactional
    public AdminResidentResponse createResident(AdminResidentCreateRequest request) {
        Room room = roomRepository.findById(request.getRoomNo())
                .orElseThrow(() -> new RuntimeException("ไม่พบห้องหมายเลข " + request.getRoomNo()));

        Resident resident = new Resident(
                room,
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNo()
        );

        Resident saved = residentRepository.save(resident);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AdminResidentResponse updateResident(UUID residentId, AdminResidentUpdateRequest request) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้พักอาศัย"));

        if (request.getFirstName() != null) resident.setFirstName(request.getFirstName());
        if (request.getLastName() != null) resident.setLastName(request.getLastName());
        if (request.getPhoneNo() != null) resident.setPhoneNo(request.getPhoneNo());

        if (request.getRoomNo() != null) {
            Room room = roomRepository.findById(request.getRoomNo())
                    .orElseThrow(() -> new RuntimeException("ไม่พบห้องหมายเลข " + request.getRoomNo()));
            resident.setRoom(room);
        }

        residentRepository.save(resident);
        return toResponse(resident);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll(Sort.by(Sort.Direction.ASC, "roomNo"));
    }

    private AdminResidentResponse toResponse(Resident resident) {
        return new AdminResidentResponse(
                resident.getResidentId(),
                resident.getFirstName(),
                resident.getLastName(),
                resident.getPhoneNo(),
                resident.getRoom() != null ? resident.getRoom().getRoomNo() : null
        );
    }
}