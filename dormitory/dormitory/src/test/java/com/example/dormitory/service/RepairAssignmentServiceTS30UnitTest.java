package com.example.dormitory.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.state.RepairRequestState;
import com.example.dormitory.domain.state.RepairRequestStateRegistry;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;
import com.example.dormitory.event.RepairAssignmentSubject;
import com.example.dormitory.event.RepairStatusSubject;
import com.example.dormitory.mapper.DailyRepairJobMapper;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairAssignmentStatusHistoryRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.TechnicianRepository;
import com.example.dormitory.service.impl.RepairAssignmentServiceImpl;

@ExtendWith(MockitoExtension.class)
class RepairAssignmentServiceTS30UnitTest {

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

    @Mock
    private Technician technician;

    @Mock 
    private DailyRepairJobMapper dailyRepairJobMapper;

    @Mock 
    private RepairRequestStateRegistry repairRequestStateRegistry;
    
    private RepairAssignmentServiceImpl service;

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        service = new RepairAssignmentServiceImpl(
                repairAssignmentRepository,
                technicianRepository,
                historyRepository,
                adminRepository,
                repairRequestService,
                repairRequestStateRegistry,
                repairStatusSubject,
                repairAssignmentSubject,
                dailyRepairJobMapper
        );
    }

    /**
     * TC-UT-30-01
     * ตรวจสอบการสรุปจำนวนงานซ่อมทั้งหมดและแยกตามสถานะ
     */
    @Test
    void shouldReturnDailySummaryForAllJobStatuses() {
        RepairAssignment completed1 = createAssignment(
                RepairRequestStatus.COMPLETED);
        RepairAssignment completed2 = createAssignment(
                RepairRequestStatus.COMPLETED);
        RepairAssignment inProgress1 = createAssignment(
                RepairRequestStatus.IN_PROGRESS);
        RepairAssignment inProgress2 = createAssignment(
                RepairRequestStatus.IN_PROGRESS);
        RepairAssignment inCompleted = createAssignment(
                RepairRequestStatus.IN_COMPLETED);

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        org.mockito.ArgumentMatchers.eq(technicianId),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any()
                ))
                .thenReturn(List.of(
                        completed1,
                        completed2,
                        inProgress1,
                        inProgress2,
                        inCompleted
                ));

        DailyRepairSummaryDto result =
                service.getDailySummary(technicianId);

        assertEquals(5L, result.getTotal());
        assertEquals(2L, result.getCompleted());
        assertEquals(2L, result.getInProgress());
        assertEquals(1L, result.getNotCompleted());

        verify(technicianRepository).findById(technicianId);
        verify(repairAssignmentRepository)
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        org.mockito.ArgumentMatchers.eq(technicianId),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any()
                );
    }

    /**
     * TC-UT-30-02
     * ตรวจสอบกรณีไม่มีงานซ่อมประจำวัน
     */
    @Test
    void shouldReturnZeroSummaryWhenNoDailyJobsExist() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        org.mockito.ArgumentMatchers.eq(technicianId),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any()
                ))
                .thenReturn(List.of());

        DailyRepairSummaryDto result =
                service.getDailySummary(technicianId);

        assertEquals(0L, result.getTotal());
        assertEquals(0L, result.getCompleted());
        assertEquals(0L, result.getInProgress());
        assertEquals(0L, result.getNotCompleted());
    }

    /**
     * TC-UT-30-03
     * ตรวจสอบกรณีมีเฉพาะงานที่เสร็จสิ้นแล้ว
     */
    @Test
    void shouldCountOnlyCompletedJobs() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        org.mockito.ArgumentMatchers.eq(technicianId),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any()
                ))
                .thenReturn(List.of(
                        createAssignment(RepairRequestStatus.COMPLETED),
                        createAssignment(RepairRequestStatus.COMPLETED),
                        createAssignment(RepairRequestStatus.COMPLETED)
                ));

        DailyRepairSummaryDto result =
                service.getDailySummary(technicianId);

        assertEquals(3L, result.getTotal());
        assertEquals(3L, result.getCompleted());
        assertEquals(0L, result.getInProgress());
        assertEquals(0L, result.getNotCompleted());
    }

    /**
     * TC-UT-30-04
     * ตรวจสอบกรณีมีเฉพาะงานที่กำลังดำเนินการ
     */
    @Test
    void shouldCountOnlyInProgressJobs() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        org.mockito.ArgumentMatchers.eq(technicianId),
                        org.mockito.ArgumentMatchers.any(),
                        org.mockito.ArgumentMatchers.any()
                ))
                .thenReturn(List.of(
                        createAssignment(RepairRequestStatus.IN_PROGRESS),
                        createAssignment(RepairRequestStatus.IN_PROGRESS)
                ));

        DailyRepairSummaryDto result =
                service.getDailySummary(technicianId);

        assertEquals(2L, result.getTotal());
        assertEquals(0L, result.getCompleted());
        assertEquals(2L, result.getInProgress());
        assertEquals(0L, result.getNotCompleted());
    }

    /**
     * TC-UT-30-05
     * ตรวจสอบกรณีไม่พบข้อมูลช่าง
     */
    @Test
    void shouldThrowExceptionWhenTechnicianDoesNotExist() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.getDailySummary(technicianId)
        );

        assertEquals(
                "ไม่พบช่างเทคนิค ID: " + technicianId,
                exception.getMessage()
        );

        verifyNoInteractions(repairAssignmentRepository);
    }

    private RepairAssignment createAssignment(
            RepairRequestStatus status
    ) {
        RepairAssignment assignment = new RepairAssignment();
        assignment.setJobStatus(status);
        return assignment;
    }
}
