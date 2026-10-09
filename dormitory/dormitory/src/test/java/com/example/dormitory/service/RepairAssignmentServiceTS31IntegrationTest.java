
package com.example.dormitory.service;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairAssignmentStatusHistory;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairAssignmentStatusHistoryRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.TechnicianRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RepairAssignmentServiceTS31IntegrationTest {

    @Autowired
    private RepairAssignmentService repairAssignmentService;

    @MockitoBean
    private RepairAssignmentRepository repairAssignmentRepository;

    @MockitoBean
    private TechnicianRepository technicianRepository;

    @MockitoBean
    private RepairAssignmentStatusHistoryRepository historyRepository;

    @MockitoBean
    private AdminRepository adminRepository;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private RepairRequestRepository repairRequestRepository;

    private UUID assignmentId;
    private UUID technicianId;
    private UUID userId;
    private UUID requestId;

    private User user;
    private Technician technician;
    private RepairRequest repairRequest;
    private RepairAssignment assignment;

    @BeforeEach
    void setUp() {
        assignmentId = UUID.fromString(
                "11111111-1111-1111-1111-111111111111");

        requestId = UUID.fromString(
                "33333333-3333-3333-3333-333333333333");

        userId = UUID.fromString(
                "44444444-4444-4444-4444-444444444444");

        technicianId = UUID.fromString(
                "55555555-5555-5555-5555-555555555555");

        user = new User();
        user.setUserId(userId);

        technician = new Technician();
        technician.setTechnicianId(technicianId);
        technician.setUser(user);

        repairRequest = new RepairRequest();
        repairRequest.setRepairRequestId(requestId);

        assignment = new RepairAssignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setTechnician(technician);
        assignment.setRepairRequest(repairRequest);
        assignment.setJobStatus(RepairRequestStatus.IN_PROGRESS);

        when(repairAssignmentRepository.findById(assignmentId))
                .thenReturn(Optional.of(assignment));

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository.save(any(RepairAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(historyRepository.save(
                any(RepairAssignmentStatusHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("TC-IT-31-01: เปลี่ยนสถานะงานซ่อมสำเร็จ")
    void shouldUpdateJobStatusSuccessfully() {
        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                "ดำเนินการซ่อมเสร็จเรียบร้อย"
        );

        assertEquals(
                RepairRequestStatus.COMPLETED,
                assignment.getJobStatus()
        );

        assertEquals(
                "ดำเนินการซ่อมเสร็จเรียบร้อย",
                assignment.getTechnicianNote()
        );

        verify(repairAssignmentRepository).save(assignment);
        verify(historyRepository)
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-IT-31-02: ไม่พบรายการมอบหมายงาน")
    void shouldThrowExceptionWhenAssignmentDoesNotExist() {
        when(repairAssignmentRepository.findById(assignmentId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repairAssignmentService.updateJobStatus(
                        assignmentId,
                        technicianId,
                        RepairRequestStatus.COMPLETED,
                        "ทดสอบ"
                )
        );

        assertEquals(
                "ไม่พบรายการมอบหมายงาน",
                exception.getMessage()
        );

        verify(technicianRepository, never()).findById(any());
        verify(historyRepository, never())
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-IT-31-03: ไม่พบข้อมูลช่างเทคนิค")
    void shouldThrowExceptionWhenTechnicianDoesNotExist() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repairAssignmentService.updateJobStatus(
                        assignmentId,
                        technicianId,
                        RepairRequestStatus.COMPLETED,
                        "ทดสอบ"
                )
        );

        assertTrue(
                exception.getMessage().contains("ไม่พบช่างเทคนิค")
        );

        verify(historyRepository, never())
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-IT-31-04: ปฏิเสธการเปลี่ยนสถานะที่ไม่ถูกต้อง")
    void shouldRejectInvalidStatusTransition() {
        RepairRequestStatus originalStatus = assignment.getJobStatus();

        assertThrows(
                IllegalStateException.class,
                () -> repairAssignmentService.updateJobStatus(
                        assignmentId,
                        technicianId,
                        RepairRequestStatus.IN_PROGRESS,
                        "ทดสอบเปลี่ยนสถานะ"
                )
        );

        assertEquals(originalStatus, assignment.getJobStatus());

        verify(historyRepository, never())
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-IT-31-05: บันทึกประวัติเมื่อเปลี่ยนสถานะสำเร็จ")
    void shouldSaveHistoryWhenStatusIsUpdated() {
        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                "ตรวจสอบประวัติ"
        );

        verify(historyRepository, times(1))
                .save(any(RepairAssignmentStatusHistory.class));

        verify(repairAssignmentRepository, times(1))
                .save(assignment);
    }
}
