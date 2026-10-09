
package com.example.dormitory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairAssignmentStatusHistory;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.state.RepairRequestState;
import com.example.dormitory.event.RepairAssignmentSubject;
import com.example.dormitory.event.RepairStatusSubject;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairAssignmentStatusHistoryRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.TechnicianRepository;

@ExtendWith(MockitoExtension.class)
class RepairAssignmentServiceTS32UnitTest {

    @Mock
    private RepairAssignmentRepository repairAssignmentRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private RepairAssignmentStatusHistoryRepository historyRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private RepairStatusSubject repairStatusSubject;

    @Mock
    private RepairAssignmentSubject repairAssignmentSubject;

    private RepairAssignmentService repairAssignmentService;

    private RepairRequestState repairRequestState;
    private List<RepairRequestState> repairRequestStates;

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

        // Mock ข้อมูลรายการมอบหมายงานและช่างเทคนิค
        when(repairAssignmentRepository.findById(assignmentId))
                .thenReturn(Optional.of(assignment));

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        // Mock State สำหรับเปลี่ยนจาก IN_PROGRESS เป็น COMPLETED
        repairRequestState = mock(RepairRequestState.class);

        when(repairRequestState.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(repairRequestState.getAllowedNext())
                .thenReturn(Set.of(RepairRequestStatus.COMPLETED));

        repairRequestStates = new ArrayList<>();
        repairRequestStates.add(repairRequestState);

        // สร้าง Service โดยส่ง State List เข้า Constructor โดยตรง
        repairAssignmentService = new RepairAssignmentService(
                repairAssignmentRepository,
                technicianRepository,
                historyRepository,
                repairRequestStates,
                adminRepository,
                repairRequestService,
                repairRequestRepository,
                repairStatusSubject,
                repairAssignmentSubject
        );

        lenient().when(
                repairAssignmentRepository.save(any(RepairAssignment.class))
        ).thenAnswer(invocation -> invocation.getArgument(0));

        lenient().when(
                historyRepository.save(any(RepairAssignmentStatusHistory.class))
        ).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void prepareValidTransition() {
        when(repairRequestState.getAllowedNext())
                .thenReturn(Set.of(RepairRequestStatus.COMPLETED));
    }

    @Test
    @DisplayName("TC-UT-32-01: บันทึกหมายเหตุการดำเนินงานซ่อมสำเร็จ")
    void shouldSaveTechnicianNoteSuccessfully() {
        prepareValidTransition();

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

        verify(repairAssignmentRepository, times(1))
                .save(assignment);

        verify(historyRepository, times(1))
                .save(any(RepairAssignmentStatusHistory.class));

        verify(repairStatusSubject, times(1))
                .notifyObservers(any());
    }

    @Test
    @DisplayName("TC-UT-32-02: บันทึกหมายเหตุเป็นข้อความว่าง")
    void shouldSaveEmptyTechnicianNote() {
        prepareValidTransition();

        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                ""
        );

        assertEquals("", assignment.getTechnicianNote());

        verify(repairAssignmentRepository).save(assignment);

        verify(historyRepository)
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-UT-32-03: บันทึกหมายเหตุเป็น null")
    void shouldHandleNullTechnicianNote() {
        prepareValidTransition();

        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                null
        );

        assertNull(assignment.getTechnicianNote());

        verify(repairAssignmentRepository).save(assignment);

        verify(historyRepository)
                .save(any(RepairAssignmentStatusHistory.class));
    }

    @Test
    @DisplayName("TC-UT-32-04: บันทึกหมายเหตุที่มีช่องว่าง")
    void shouldSaveTechnicianNoteContainingWhitespace() {
        prepareValidTransition();

        String note = "   ";

        repairAssignmentService.updateJobStatus(
                assignmentId,
                technicianId,
                RepairRequestStatus.COMPLETED,
                note
        );

        assertEquals(note, assignment.getTechnicianNote());

        verify(repairAssignmentRepository).save(assignment);
    }

    @Test
    @DisplayName("TC-UT-32-05: ไม่บันทึกหมายเหตุเมื่อเปลี่ยนสถานะไม่ถูกต้อง")
    void shouldNotSaveNoteWhenStatusTransitionIsInvalid() {
        String originalNote = "หมายเหตุเดิม";
        assignment.setTechnicianNote(originalNote);

        when(repairRequestState.getAllowedNext())
                .thenReturn(Set.of(RepairRequestStatus.IN_COMPLETED));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repairAssignmentService.updateJobStatus(
                        assignmentId,
                        technicianId,
                        RepairRequestStatus.COMPLETED,
                        "หมายเหตุใหม่"
                )
        );

        assertTrue(exception.getMessage().contains(
                "ไม่สามารถเปลี่ยนสถานะ"));

        assertEquals(originalNote, assignment.getTechnicianNote());

        verify(repairAssignmentRepository, never())
                .save(any(RepairAssignment.class));

        verify(historyRepository, never())
                .save(any(RepairAssignmentStatusHistory.class));

        verify(repairStatusSubject, never())
                .notifyObservers(any());
    }
}
