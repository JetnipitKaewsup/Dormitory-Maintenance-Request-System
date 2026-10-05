package com.example.dormitory.service;

import com.example.dormitory.dto.adminTechnician.*;
import com.example.dormitory.model.Technician;
import com.example.dormitory.model.User;
import com.example.dormitory.repository.AdminTechnicianRepository;
import com.example.dormitory.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminTechnicianServiceImpl implements AdminTechnicianService {

    private final AdminTechnicianRepository technicianRepository;
    private final UserRepository userRepository;
    private final WebClient supabaseWebClient;

    public AdminTechnicianServiceImpl(AdminTechnicianRepository technicianRepository,
                                       UserRepository userRepository,
                                       WebClient supabaseWebClient) {
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
        this.supabaseWebClient = supabaseWebClient;
    }

    @Override
    public List<AdminTechnicianResponse> getAllTechnicians() {
        return technicianRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminTechnicianResponse getTechnicianById(UUID technicianId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลช่าง"));
        return toResponse(technician);
    }

    @Override
    @Transactional
    public AdminTechnicianResponse updateTechnician(UUID technicianId, AdminTechnicianUpdateRequest request) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลช่าง"));

        User user = technician.getUser();
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNo() != null) user.setPhoneNo(request.getPhoneNo());
        if (request.getSpecialization() != null) technician.setSpecialization(request.getSpecialization());

        userRepository.save(user);
        technicianRepository.save(technician);

        return toResponse(technician);
    }

    @Override
    @Transactional
    public AdminTechnicianResponse createTechnician(AdminTechnicianCreateRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("username นี้ถูกใช้งานแล้ว");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("first_name", request.getFirstName());
        metadata.put("last_name", request.getLastName());
        metadata.put("username", request.getUsername());
        metadata.put("phone_no", request.getPhoneNo());
        metadata.put("role", "TECHNICIAN");
        body.put("data", metadata);

        Map<String, Object> supabaseResponse = supabaseWebClient
                .post()
                .uri("/auth/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Object idObj = supabaseResponse != null ? supabaseResponse.get("id") : null;
        if (idObj == null) {
            throw new RuntimeException("สร้างบัญชีที่ Supabase ไม่สำเร็จ");
        }
        UUID supabaseUserId = UUID.fromString(idObj.toString());

        User user = new User();
        user.setUserId(supabaseUserId);
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNo(request.getPhoneNo());
        user.setRole("TECHNICIAN");

        User savedUser = userRepository.save(user);

        Technician technician = new Technician(null, savedUser, request.getSpecialization());
        Technician savedTechnician = technicianRepository.save(technician);

        return toResponse(savedTechnician);
    }

    private AdminTechnicianResponse toResponse(Technician technician) {
        User user = technician.getUser();
        return new AdminTechnicianResponse(
                technician.getTechnicianId(),
                technician.getSpecialization(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNo(),
                user.getUsername()
        );
    }
}