package com.example.dormitory.service.impl;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairAssignmentStatusHistory;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.dto.request.AdminTechnicianCreateRequest;
import com.example.dormitory.dto.request.AdminTechnicianUpdateRequest;
import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.dto.response.AdminTechnicianHistoryResponse;
import com.example.dormitory.repository.AdminTechnicianRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairAssignmentStatusHistoryRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.AdminTechnicianService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

import java.time.LocalDateTime;
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
    private final RepairAssignmentRepository repairAssignmentRepository;
    private final RepairAssignmentStatusHistoryRepository statusHistoryRepository;

    public AdminTechnicianServiceImpl(AdminTechnicianRepository technicianRepository,
                                      UserRepository userRepository,
                                      WebClient supabaseWebClient,
                                      RepairAssignmentRepository repairAssignmentRepository,
                                      RepairAssignmentStatusHistoryRepository statusHistoryRepository) {
        this.technicianRepository = technicianRepository;
        this.userRepository = userRepository;
        this.supabaseWebClient = supabaseWebClient;
        this.repairAssignmentRepository = repairAssignmentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
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
        // 1. เช็ค username ซ้ำ
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("username นี้ถูกใช้งานแล้ว");
        }

        // 2. สร้าง body สำหรับ Supabase signup
        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("first_name", request.getFirstName());
        metadata.put("last_name", request.getLastName());
        metadata.put("username", request.getUsername());
        metadata.put("phone_no", request.getPhoneNo());
        metadata.put("role", "TECHNICIAN");
        metadata.put("specialization", request.getSpecialization());
        body.put("data", metadata);

        // 3. เรียก Supabase signup → trigger handle_new_user จะสร้าง users + technician ให้
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

        // 4. trigger สร้างข้อมูลให้แล้ว → แค่ query กลับมา
        Technician technician = technicianRepository
                .findByUser_UserId(supabaseUserId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลช่างที่เพิ่งสร้าง"));

        return toResponse(technician);
    }

    @Override
    public List<AdminTechnicianHistoryResponse> getRepairHistoryByTechnicianId(UUID technicianId) {
        technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลช่าง"));

        List<RepairAssignment> assignments =
                repairAssignmentRepository.findByTechnician_TechnicianIdOrderByAssignDateDesc(technicianId);

        return assignments.stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());
    }

    private AdminTechnicianHistoryResponse toHistoryResponse(RepairAssignment assignment) {
        RepairRequest req = assignment.getRepairRequest();

        LocalDateTime completeDate = statusHistoryRepository
                .findFirstByAssignment_AssignmentIdAndNewStatusOrderByChangeDateDesc(
                        assignment.getAssignmentId(),
                        RepairRequestStatus.COMPLETED)
                .map(RepairAssignmentStatusHistory::getChangeDate)
                .orElse(null);

        return new AdminTechnicianHistoryResponse(
                assignment.getAssignmentId(),
                req != null ? req.getRepairRequestId() : null,
                req != null && req.getRepairType() != null
                        ? req.getRepairType().name() : null,
                req != null ? req.getDescription() : null,
                assignment.getJobStatus() != null ? assignment.getJobStatus().getThaiName() : null,
                assignment.getJobStatus() != null ? assignment.getJobStatus().getCssClass() : null,
                assignment.getAdminNote(),
                assignment.getTechnicianNote(),
                assignment.getAssignDate(),
                // reportDate = วันที่แจ้ง
                req != null ? req.getCreatedAt() : null,
                // completeDate = วันที่ tech กดเสร็จจริง
                completeDate
        );
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