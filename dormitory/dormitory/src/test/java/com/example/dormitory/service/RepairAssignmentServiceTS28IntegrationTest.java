
package com.example.dormitory.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.Building;
import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.enums.RepairType;
import com.example.dormitory.dto.response.DailyRepairJobDto;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.TechnicianRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class RepairAssignmentServiceTS28IntegrationTest {

    @Autowired
    private RepairAssignmentService repairAssignmentService;

    @Autowired
    private RepairAssignmentRepository repairAssignmentRepository;

    @Autowired
    private RepairRequestRepository repairRequestRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private EntityManager entityManager;

    private Technician technician;
    private Admin admin;
    private Reporter reporter;
    private Room room;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        LocalDate today = LocalDate.now();
        startTime = today.atTime(9, 0);
        endTime = today.atTime(10, 0);

        // 1. สร้าง User ของช่าง
        User technicianUser = createUser("TECHNICIAN");
        technician = new Technician();
        technician.setUser(technicianUser);
        technician.setSpecialization("General");
        entityManager.persist(technician);

        // 2. สร้าง User ของ Admin
        User adminUser = createUser("ADMIN");
        admin = new Admin(adminUser);
        entityManager.persist(admin);

        // 3. สร้างอาคารและห้อง โดยไม่ต้องมี BuildingRepository
        Building building = new Building(
                9901,
                "Integration Test Building",
                5
        );
        entityManager.persist(building);

        room = new Room(9901, building);
        entityManager.persist(room);

        // 4. สร้าง Resident
        Resident resident = new Resident(
                room,
                "Integration",
                "Resident",
                "0834567890"
        );
        entityManager.persist(resident);

        // 5. สร้าง User และ Reporter
        User reporterUser = createUser("REPORTER");
        reporter = new Reporter(reporterUser, resident);
        entityManager.persist(reporter);

        // บันทึกข้อมูลทั้งหมดก่อนเรียก Service
        entityManager.flush();
        entityManager.clear();
    }

    private User createUser(String role) {
        String unique = UUID.randomUUID().toString();

        User user = new User();
        user.setUserId(UUID.randomUUID());
        user.setFirstName("Integration");
        user.setLastName(role);
        user.setUsername("it_" + role.toLowerCase() + "_" + unique);
        user.setPassword("test-password");
        user.setPhoneNo("0812345678");
        user.setRole(role);
        user.setEmail("it_" + unique + "@example.com");

        entityManager.persist(user);
        return user;
    }

    private RepairAssignment createAssignment(
            RepairRequestStatus status,
            LocalDateTime assignDate,
            String description
    ) {
        RepairRequest request = new RepairRequest();
        request.setReporter(reporter);
        request.setAdmin(admin);
        request.setRoom(room);
        request.setRepairType(RepairType.values()[0]);
        request.setStatus(status);
        request.setDescription(description);
        request.setReporterNote("Integration reporter note");
        request.setStartDateTime(startTime);
        request.setEndDateTime(endTime);
        request.setCreatedAt(LocalDateTime.now());

        repairRequestRepository.saveAndFlush(request);

        RepairAssignment assignment = new RepairAssignment();
        assignment.setRepairRequest(request);
        assignment.setTechnician(technician);
        assignment.setAdmin(admin);
        assignment.setJobStatus(status);
        assignment.setAdminNote("Integration admin note");
        assignment.setAssignDate(assignDate);

        return repairAssignmentRepository.saveAndFlush(assignment);
    }

    @Test
    @DisplayName("TC-IT-28-01 ดึงรายการงานซ่อมประจำวันจากฐานข้อมูลจริง")
    void TC_IT_28_01_getDailyJobsFromDatabase() {
        RepairAssignment saved = createAssignment(
                RepairRequestStatus.IN_PROGRESS,
                LocalDateTime.now(),
                "ทดสอบงานซ่อมประจำวัน"
        );

        List<DailyRepairJobDto> result =
                repairAssignmentService.getDailyJobs(
                        technician.getTechnicianId()
                );

        assertNotNull(result);

        DailyRepairJobDto dto = result.stream()
                .filter(job ->
                        job.assignmentId().equals(saved.getAssignmentId()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("ไม่พบงานที่สร้างไว้ในผลลัพธ์"));

        assertEquals("ทดสอบงานซ่อมประจำวัน", dto.description());
        assertEquals("Integration Test Building", dto.building());
        assertEquals("ห้อง 9901", dto.room());
        assertEquals("IN_PROGRESS", dto.status());
        assertEquals("Integration admin note", dto.adminNote());
        assertEquals("Integration reporter note", dto.reporterNote());
    }

    @Test
    @DisplayName("TC-IT-28-02 กรองเฉพาะงานที่เสร็จสิ้น")
    void TC_IT_28_02_filterCompletedJobs() {
        RepairAssignment completed = createAssignment(
                RepairRequestStatus.COMPLETED,
                LocalDateTime.now(),
                "งานที่เสร็จแล้ว"
        );

        createAssignment(
                RepairRequestStatus.IN_PROGRESS,
                LocalDateTime.now(),
                "งานที่กำลังดำเนินการ"
        );

        List<DailyRepairJobDto> result =
                repairAssignmentService.getDailyJobs(
                        technician.getTechnicianId(),
                        RepairJobFilter.COMPLETED
                );

        assertTrue(result.stream().anyMatch(
                job -> job.assignmentId().equals(completed.getAssignmentId())
        ));

        assertTrue(result.stream().allMatch(
                job -> "COMPLETED".equals(job.status())
        ));
    }

    @Test
    @DisplayName("TC-IT-28-03 กรณีไม่มีงานซ่อมประจำวัน")
    void TC_IT_28_03_noDailyJobs() {
        List<DailyRepairJobDto> result =
                repairAssignmentService.getDailyJobs(
                        technician.getTechnicianId()
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TC-IT-28-04 กรณีไม่พบช่างเทคนิค")
    void TC_IT_28_04_technicianNotFound() {
        UUID unknownTechnicianId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> repairAssignmentService.getDailyJobs(
                        unknownTechnicianId
                )
        );
    }
}
