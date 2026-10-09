package com.example.dormitory.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.RepairRequestStatusHistory;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.state.RepairRequestStateRegistry;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.RepairRequestStatusHistoryRepository;
import com.example.dormitory.repository.ReporterRepository;

@ExtendWith(MockitoExtension.class)
class RepairRequestServiceImplTest {

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private RepairRequestStatusHistoryRepository historyRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ReporterRepository reporterRepository;

    @Mock
    private RepairRequestStateRegistry repairRequestStateRegistry;

    @InjectMocks
    private RepairRequestServiceImpl repairRequestService;

    private UUID userId;
    private UUID repairRequestId;
    private UUID otherRepairRequestId;

    private RepairRequest repairRequest;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        repairRequestId = UUID.randomUUID();
        otherRepairRequestId = UUID.randomUUID();

        repairRequest = mock(RepairRequest.class);
    }

    // ============================================================
    // TC-JU-07-01
    // ตรวจสอบสถานะปัจจุบันของคำร้องแจ้งซ่อม
    // ============================================================

    @Test
    void TC_JU_07_01_shouldReturnCurrentRepairRequestStatus() {

        when(
            repairRequestRepository
                .findByRepairRequestIdAndReporter_User_UserId(
                    repairRequestId,
                    userId
                )
        ).thenReturn(Optional.of(repairRequest));

        when(repairRequest.getStatus())
                .thenReturn(RepairRequestStatus.PENDING);

        RepairRequest result =
                repairRequestService.getMyRequest(
                    userId,
                    repairRequestId
                );

        assertNotNull(result);

        assertEquals(
            RepairRequestStatus.PENDING,
            result.getStatus()
        );

        verify(
            repairRequestRepository
        ).findByRepairRequestIdAndReporter_User_UserId(
            repairRequestId,
            userId
        );
    }

    // ============================================================
    // TC-JU-07-02
    // ตรวจสอบการแสดงประวัติการเปลี่ยนสถานะ
    // ============================================================

    @Test
    void TC_JU_07_02_shouldReturnRepairRequestStatusHistoryInOrder() {

        RepairRequestStatusHistory history1 =
                mock(RepairRequestStatusHistory.class);

        RepairRequestStatusHistory history2 =
                mock(RepairRequestStatusHistory.class);

        RepairRequestStatusHistory history3 =
                mock(RepairRequestStatusHistory.class);

        List<RepairRequestStatusHistory> histories =
                Arrays.asList(
                    history1,
                    history2,
                    history3
                );

        // ยืนยันว่า request เป็นของ user คนนี้
        when(
            repairRequestRepository
                .findByRepairRequestIdAndReporter_User_UserId(
                    repairRequestId,
                    userId
                )
        ).thenReturn(Optional.of(repairRequest));

        // คืนประวัติสถานะ
        when(
            historyRepository
                .findByRepairRequest_RepairRequestIdOrderByChangeDateAsc(
                    repairRequestId
                )
        ).thenReturn(histories);

        List<RepairRequestStatusHistory> result =
                repairRequestService.getRequestHistory(
                    userId,
                    repairRequestId
                );

        assertNotNull(result);

        assertEquals(3, result.size());

        assertSame(history1, result.get(0));
        assertSame(history2, result.get(1));
        assertSame(history3, result.get(2));

        // ตรวจสอบว่ามีการตรวจสอบ ownership ก่อน
        verify(
            repairRequestRepository
        ).findByRepairRequestIdAndReporter_User_UserId(
            repairRequestId,
            userId
        );

        // ตรวจสอบว่ามีการดึงประวัติ
        verify(
            historyRepository
        ).findByRepairRequest_RepairRequestIdOrderByChangeDateAsc(
            repairRequestId
        );
    }

    // ============================================================
    // TC-JU-07-03
    // ตรวจสอบการเข้าถึงคำร้องของผู้ใช้งานอื่น
    // ============================================================

    @Test
    void TC_JU_07_03_shouldRejectAccessToAnotherUsersRepairRequest() {

        /*
         * User A พยายามเข้าถึงคำร้องของ User B
         *
         * Repository จะไม่พบข้อมูล เพราะ query ตรวจสอบทั้ง
         * repairRequestId และ userId
         */
        when(
            repairRequestRepository
                .findByRepairRequestIdAndReporter_User_UserId(
                    otherRepairRequestId,
                    userId
                )
        ).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> repairRequestService.getRequestHistory(
                        userId,
                        otherRepairRequestId
                    )
                );

        assertEquals(
            "ไม่พบคำร้องแจ้งซ่อม",
            exception.getMessage()
        );

        /*
         * เนื่องจากไม่มีสิทธิ์เข้าถึงคำร้อง
         * ระบบต้องไม่เรียก History Repository
         */
        verify(
            historyRepository,
            never()
        ).findByRepairRequest_RepairRequestIdOrderByChangeDateAsc(
            otherRepairRequestId
        );
    }

    // ============================================================
    // TC-JU-07-04
    // ตรวจสอบกรณีไม่พบประวัติการเปลี่ยนสถานะ
    // ============================================================

    @Test
    void TC_JU_07_04_shouldReturnEmptyHistoryWithoutException() {

        when(
            repairRequestRepository
                .findByRepairRequestIdAndReporter_User_UserId(
                    repairRequestId,
                    userId
                )
        ).thenReturn(Optional.of(repairRequest));

        when(
            historyRepository
                .findByRepairRequest_RepairRequestIdOrderByChangeDateAsc(
                    repairRequestId
                )
        ).thenReturn(Collections.emptyList());

        List<RepairRequestStatusHistory> result =
                repairRequestService.getRequestHistory(
                    userId,
                    repairRequestId
                );

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(
            repairRequestRepository
        ).findByRepairRequestIdAndReporter_User_UserId(
            repairRequestId,
            userId
        );

        verify(
            historyRepository
        ).findByRepairRequest_RepairRequestIdOrderByChangeDateAsc(
            repairRequestId
        );
    }
}
