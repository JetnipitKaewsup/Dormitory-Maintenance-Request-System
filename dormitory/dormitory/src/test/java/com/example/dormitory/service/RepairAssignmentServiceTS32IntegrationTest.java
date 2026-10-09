
package com.example.dormitory.service;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

@SpringBootTest
class RepairAssignmentServiceTS32IntegrationTest {

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
    private UUID requestId;
    private UUID userId;
    private UUID technicianId;

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
        assignment.setTechnicianNote("หมายเหตุเดิม");

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
    @DisplayName("TC-IT-32-01: บันทึกหมายเหตุและเปลี่ยนสถานะสำเร็จ")
    void shouldSaveNoteAndUpdateStatusSuccessfully() {
        String note = "เปลี่ยนอะไหล่และทดสอบการใช้งานเรียบร้อย";

        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                note
        );

        assertEquals(
                RepairRequestStatus.COMPLETED,
                assignment.getJobStatus()
        );
        assertEquals(note, assignment.getTechnicianNote());

        verify(repairAssignmentRepository)
                .save(assignment);

        verify(historyRepository)
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-IT-32-02: บันทึกหมายเหตุเป็นข้อความว่าง")
    void shouldSaveEmptyNote() {
        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                ""
        );

        assertEquals("", assignment.getTechnicianNote());
        assertEquals(
                RepairRequestStatus.COMPLETED,
                assignment.getJobStatus()
        );

        verify(repairAssignmentRepository)
                .save(assignment);

        verify(historyRepository)
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-IT-32-03: รองรับหมายเหตุที่เป็น null")
    void shouldHandleNullNote() {
        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                null
        );

        assertNull(assignment.getTechnicianNote());
        assertEquals(
                RepairRequestStatus.COMPLETED,
                assignment.getJobStatus()
        );

        verify(repairAssignmentRepository)
                .save(assignment);

        verify(historyRepository)
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-IT-32-04: บันทึกหมายเหตุที่มีเฉพาะช่องว่าง")
    void shouldSaveWhitespaceNote() {
        String note = "   ";

        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                note
        );

        assertEquals(note, assignment.getTechnicianNote());

        verify(repairAssignmentRepository)
                .save(assignment);
    }

    @Test
    @DisplayName("TC-IT-32-05: ปฏิเสธสถานะที่ไม่อนุญาตและไม่บันทึกหมายเหตุใหม่")
    void shouldRejectInvalidStatusTransition() {
        String originalNote = "หมายเหตุเดิม";
        assignment.setTechnicianNote(originalNote);

        assertThrows(
                IllegalStateException.class,
                () -> repairAssignmentService.updateJobStatus(
                        assignmentId,
                        technicianId,
                        RepairRequestStatus.PENDING,
                        "หมายเหตุใหม่"
                )
        );

        assertEquals(originalNote, assignment.getTechnicianNote());
        assertEquals(
                RepairRequestStatus.IN_PROGRESS,
                assignment.getJobStatus()
        );

        verify(repairAssignmentRepository, never())
                .save(any(RepairAssignment.class));

        verify(historyRepository, never())
                .save(any(RepairAssignmentStatusHistory.class));
    }
}
