package com.example.dormitory.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.dto.response.RepairRequestHistoryResponse;
import com.example.dormitory.repository.AdminReporterRepository;
import com.example.dormitory.repository.RepairRequestRepository;

@ExtendWith(MockitoExtension.class)
class AdminReporterServiceImplTS16Test {

    @Mock
    private AdminReporterRepository reporterRepository;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    private AdminReporterServiceImpl adminReporterService;

    @BeforeEach
    void setUp() {
        adminReporterService = new AdminReporterServiceImpl(
                reporterRepository,
                repairRequestRepository
        );
    }

    // TC-UT-16-01: พบประวัติการแจ้งซ่อมและตรวจสอบข้อมูลที่ส่งกลับ
    @Test
    void TC_UT_16_01_shouldReturnRepairHistoryWithCorrectFields() {
        UUID reporterId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 9, 30);
        LocalDateTime startAt = LocalDateTime.of(2026, 10, 2, 10, 0);
        LocalDateTime endAt = LocalDateTime.of(2026, 10, 2, 12, 0);

        Reporter reporter = mock(Reporter.class);
        RepairRequest request = mock(RepairRequest.class);

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        when(repairRequestRepository
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId))
                .thenReturn(List.of(request));

        when(request.getRepairRequestId()).thenReturn(requestId);
        when(request.getRepairType()).thenReturn(null);
        when(request.getStatus()).thenReturn(null);
        when(request.getDescription()).thenReturn("ตรวจสอบระบบไฟฟ้า");
        when(request.getCreatedAt()).thenReturn(createdAt);
        when(request.getStartDateTime()).thenReturn(startAt);
        when(request.getEndDateTime()).thenReturn(endAt);

        List<RepairRequestHistoryResponse> result =
                adminReporterService.getRepairHistoryByReporterId(reporterId);

        assertEquals(1, result.size());

        RepairRequestHistoryResponse history = result.get(0);
        assertEquals(requestId, history.getRepairRequestId());
        assertNull(history.getRepairType());
        assertNull(history.getStatus());
        assertEquals("ตรวจสอบระบบไฟฟ้า", history.getDescription());
        assertEquals(createdAt, history.getCreatedAt());
        assertEquals(startAt, history.getStartDateTime());
        assertEquals(endAt, history.getEndDateTime());

        verify(reporterRepository).findById(reporterId);
        verify(repairRequestRepository)
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId);
    }

    // TC-UT-16-02: มีหลายคำร้องและต้องส่งกลับครบทุกคำร้อง
    @Test
    void TC_UT_16_02_shouldReturnAllRepairRequests() {
        UUID reporterId = UUID.randomUUID();

        Reporter reporter = mock(Reporter.class);
        RepairRequest request1 = mock(RepairRequest.class);
        RepairRequest request2 = mock(RepairRequest.class);

        UUID requestId1 = UUID.randomUUID();
        UUID requestId2 = UUID.randomUUID();

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        when(repairRequestRepository
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId))
                .thenReturn(List.of(request1, request2));

        when(request1.getRepairRequestId()).thenReturn(requestId1);
        when(request2.getRepairRequestId()).thenReturn(requestId2);

        List<RepairRequestHistoryResponse> result =
                adminReporterService.getRepairHistoryByReporterId(reporterId);

        assertEquals(2, result.size());
        assertEquals(requestId1, result.get(0).getRepairRequestId());
        assertEquals(requestId2, result.get(1).getRepairRequestId());
    }

    // TC-UT-16-03: ผู้แจ้งซ่อมมีอยู่ แต่ไม่มีประวัติการแจ้งซ่อม
    @Test
    void TC_UT_16_03_shouldReturnEmptyListWhenNoHistoryExists() {
        UUID reporterId = UUID.randomUUID();
        Reporter reporter = mock(Reporter.class);

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        when(repairRequestRepository
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId))
                .thenReturn(List.of());

        List<RepairRequestHistoryResponse> result =
                adminReporterService.getRepairHistoryByReporterId(reporterId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // TC-UT-16-04: ไม่พบผู้แจ้งซ่อม ต้องแจ้งข้อผิดพลาด
    @Test
    void TC_UT_16_04_shouldThrowExceptionWhenReporterDoesNotExist() {
        UUID reporterId = UUID.randomUUID();

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> adminReporterService.getRepairHistoryByReporterId(reporterId)
        );

        verify(repairRequestRepository, never())
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId);
    }

    // TC-UT-16-05: เมื่อประเภทงานและสถานะเป็น null ต้องแปลงข้อมูลได้โดยไม่เกิดข้อผิดพลาด
    @Test
    void TC_UT_16_05_shouldHandleNullRepairTypeAndStatus() {
        UUID reporterId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        Reporter reporter = mock(Reporter.class);
        RepairRequest request = mock(RepairRequest.class);

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        when(repairRequestRepository
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId))
                .thenReturn(List.of(request));

        when(request.getRepairRequestId()).thenReturn(requestId);
        when(request.getRepairType()).thenReturn(null);
        when(request.getStatus()).thenReturn(null);

        List<RepairRequestHistoryResponse> result =
                adminReporterService.getRepairHistoryByReporterId(reporterId);

        assertEquals(1, result.size());
        assertNull(result.get(0).getRepairType());
        assertNull(result.get(0).getStatus());
    }
}
