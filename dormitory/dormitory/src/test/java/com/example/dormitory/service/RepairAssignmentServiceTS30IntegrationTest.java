package com.example.dormitory.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;
import com.example.dormitory.event.RepairAssignmentSubject;
import com.example.dormitory.event.RepairStatusSubject;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairAssignmentStatusHistoryRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.TechnicianRepository;

@SpringBootTest
class RepairAssignmentServiceTS30IntegrationTest {

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

    @MockitoBean
    private RepairStatusSubject repairStatusSubject;

    @MockitoBean
    private RepairAssignmentSubject repairAssignmentSubject;

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    /**
     * TC-IT-30-01
     * ตรวจสอบการสรุปงานซ่อมทั้งหมดแยกตามสถานะ
     */
    @Test
    void shouldReturnDailySummaryForAllJobStatuses() {
        Technician technician = new Technician();

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        eq(technicianId), any(), any()))
                .thenReturn(List.of(
                        createAssignment(RepairRequestStatus.COMPLETED),
                        createAssignment(RepairRequestStatus.COMPLETED),
                        createAssignment(RepairRequestStatus.IN_PROGRESS),
                        createAssignment(RepairRequestStatus.IN_PROGRESS),
                        createAssignment(RepairRequestStatus.IN_COMPLETED)
                ));

        DailyRepairSummaryDto result =
                repairAssignmentService.getDailySummary(technicianId);

        assertEquals(5L, result.getTotal());
        assertEquals(2L, result.getCompleted());
        assertEquals(2L, result.getInProgress());
        assertEquals(1L, result.getNotCompleted());

        verify(technicianRepository).findById(technicianId);
        verify(repairAssignmentRepository)
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        eq(technicianId), any(), any());
    }

    /**
     * TC-IT-30-02
     * ตรวจสอบกรณีไม่มีงานซ่อมประจำวัน
     */
    @Test
    void shouldReturnZeroSummaryWhenNoDailyJobsExist() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(new Technician()));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        eq(technicianId), any(), any()))
                .thenReturn(List.of());

        DailyRepairSummaryDto result =
                repairAssignmentService.getDailySummary(technicianId);

        assertEquals(0L, result.getTotal());
        assertEquals(0L, result.getCompleted());
        assertEquals(0L, result.getInProgress());
        assertEquals(0L, result.getNotCompleted());
    }

    /**
     * TC-IT-30-03
     * ตรวจสอบกรณีมีเฉพาะงานที่เสร็จสิ้นแล้ว
     */
    @Test
    void shouldCountOnlyCompletedJobs() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(new Technician()));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        eq(technicianId), any(), any()))
                .thenReturn(List.of(
                        createAssignment(RepairRequestStatus.COMPLETED),
                        createAssignment(RepairRequestStatus.COMPLETED),
                        createAssignment(RepairRequestStatus.COMPLETED)
                ));

        DailyRepairSummaryDto result =
                repairAssignmentService.getDailySummary(technicianId);

        assertEquals(3L, result.getTotal());
        assertEquals(3L, result.getCompleted());
        assertEquals(0L, result.getInProgress());
        assertEquals(0L, result.getNotCompleted());
    }

    /**
     * TC-IT-30-04
     * ตรวจสอบกรณีมีเฉพาะงานที่กำลังดำเนินการ
     */
    @Test
    void shouldCountOnlyInProgressJobs() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(new Technician()));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        eq(technicianId), any(), any()))
                .thenReturn(List.of(
                        createAssignment(RepairRequestStatus.IN_PROGRESS),
                        createAssignment(RepairRequestStatus.IN_PROGRESS)
                ));

        DailyRepairSummaryDto result =
                repairAssignmentService.getDailySummary(technicianId);

        assertEquals(2L, result.getTotal());
        assertEquals(0L, result.getCompleted());
        assertEquals(2L, result.getInProgress());
        assertEquals(0L, result.getNotCompleted());
    }

    /**
     * TC-IT-30-05
     * ตรวจสอบกรณีไม่พบข้อมูลช่าง
     */
    @Test
    void shouldThrowExceptionWhenTechnicianDoesNotExist() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repairAssignmentService.getDailySummary(technicianId)
        );

        assertEquals(
                "ไม่พบช่างเทคนิค ID: " + technicianId,
                exception.getMessage()
        );

        verifyNoInteractions(repairAssignmentRepository);
    }

    private RepairAssignment createAssignment(
            RepairRequestStatus status) {

        RepairAssignment assignment = new RepairAssignment();
        assignment.setJobStatus(status);
        return assignment;
    }
}
