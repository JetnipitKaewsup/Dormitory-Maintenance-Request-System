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
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.enums.RepairType;
import com.example.dormitory.dto.response.RepairRequestHistoryResponse;
import com.example.dormitory.repository.AdminReporterRepository;
import com.example.dormitory.repository.RepairRequestRepository;

@ExtendWith(MockitoExtension.class)
class AdminReporterServiceTS23UnitTest {

    @Mock
    private AdminReporterRepository reporterRepository;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @InjectMocks
    private AdminReporterServiceImpl adminReporterService;

    private UUID reporterId;
    private UUID requestId;
    private Reporter reporter;

    @BeforeEach
    void setUp() {
        reporterId = UUID.randomUUID();
        requestId = UUID.randomUUID();
        reporter = mock(Reporter.class);
    }

    // TC-UT-23-01:
    // ตรวจสอบการดึงและแปลงข้อมูลประวัติการแจ้งซ่อมสำเร็จ
    @Test
    void TC_UT_23_01_shouldReturnMappedRepairHistory() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 1, 10, 30);
        LocalDateTime startDateTime = LocalDateTime.of(2026, 10, 2, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 10, 2, 11, 0);

        RepairRequest request = mock(RepairRequest.class);

        when(request.getRepairRequestId()).thenReturn(requestId);
        when(request.getRepairType()).thenReturn(RepairType.values()[0]);
        when(request.getStatus()).thenReturn(RepairRequestStatus.values()[0]);
        when(request.getDescription()).thenReturn("ทดสอบประวัติการแจ้งซ่อม");
        when(request.getCreatedAt()).thenReturn(createdAt);
        when(request.getStartDateTime()).thenReturn(startDateTime);
        when(request.getEndDateTime()).thenReturn(endDateTime);

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        when(repairRequestRepository
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId))
                .thenReturn(List.of(request));

        List<RepairRequestHistoryResponse> result =
                adminReporterService.getRepairHistoryByReporterId(reporterId);

        assertNotNull(result);
        assertEquals(1, result.size());

        RepairRequestHistoryResponse response = result.get(0);

        assertEquals(requestId, response.getRepairRequestId());
        assertEquals(RepairType.values()[0].name(), response.getRepairType());
        assertEquals(RepairRequestStatus.values()[0].name(), response.getStatus());
        assertEquals("ทดสอบประวัติการแจ้งซ่อม", response.getDescription());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(startDateTime, response.getStartDateTime());
        assertEquals(endDateTime, response.getEndDateTime());

        verify(reporterRepository).findById(reporterId);
        verify(repairRequestRepository)
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId);
    }

    // TC-UT-23-02:
    // ตรวจสอบกรณีผู้แจ้งมีอยู่ในระบบ แต่ไม่มีประวัติการแจ้งซ่อม
    @Test
    void TC_UT_23_02_shouldReturnEmptyListWhenNoHistoryExists() {
        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        when(repairRequestRepository
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId))
                .thenReturn(List.of());

        List<RepairRequestHistoryResponse> result =
                adminReporterService.getRepairHistoryByReporterId(reporterId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(reporterRepository).findById(reporterId);
        verify(repairRequestRepository)
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId);
    }

    // TC-UT-23-03:
    // ตรวจสอบกรณีไม่พบผู้แจ้งตาม ID ที่ระบุ
    @Test
    void TC_UT_23_03_shouldThrowExceptionWhenReporterDoesNotExist() {
        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> adminReporterService.getRepairHistoryByReporterId(reporterId)
        );

        verify(reporterRepository).findById(reporterId);

        verify(repairRequestRepository, never())
                .findByReporter_ReporterIdOrderByCreatedAtDesc(any(UUID.class));
    }

    // TC-UT-23-04:
    // ตรวจสอบการแปลงข้อมูลเมื่อประเภทงาน สถานะ และวันเวลาเป็น null
    @Test
    void TC_UT_23_04_shouldMapNullableFieldsCorrectly() {
        RepairRequest request = mock(RepairRequest.class);

        when(request.getRepairRequestId()).thenReturn(requestId);
        when(request.getRepairType()).thenReturn(null);
        when(request.getStatus()).thenReturn(null);
        when(request.getDescription()).thenReturn("ข้อมูลบางส่วน");
        when(request.getCreatedAt()).thenReturn(null);
        when(request.getStartDateTime()).thenReturn(null);
        when(request.getEndDateTime()).thenReturn(null);

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        when(repairRequestRepository
                .findByReporter_ReporterIdOrderByCreatedAtDesc(reporterId))
                .thenReturn(List.of(request));

        List<RepairRequestHistoryResponse> result =
                adminReporterService.getRepairHistoryByReporterId(reporterId);

        assertEquals(1, result.size());

        RepairRequestHistoryResponse response = result.get(0);

        assertEquals(requestId, response.getRepairRequestId());
        assertNull(response.getRepairType());
        assertNull(response.getStatus());
        assertEquals("ข้อมูลบางส่วน", response.getDescription());
        assertNull(response.getCreatedAt());
        assertNull(response.getStartDateTime());
        assertNull(response.getEndDateTime());
    }
}
