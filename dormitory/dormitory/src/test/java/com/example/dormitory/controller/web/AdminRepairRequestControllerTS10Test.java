package com.example.dormitory.controller.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

/**
 * TS-10
 * ดูรายละเอียดคำร้อง
 *
 * Unit Test สำหรับ AdminRepairRequestController
 */
class AdminRepairRequestControllerTS10Test {

    private RepairRequestService repairRequestService;
    private RepairAssignmentService repairAssignmentService;
    private AdminRepairRequestController controller;
    private Model model;

    @BeforeEach
    void setUp() {

        repairRequestService =
                mock(RepairRequestService.class);

        repairAssignmentService =
                mock(RepairAssignmentService.class);

        model = mock(Model.class);

        controller =
                new AdminRepairRequestController(
                        repairRequestService,
                        repairAssignmentService
                );
    }

    /**
     * TC-UT-10-01
     * ตรวจสอบการแสดงรายละเอียดคำร้อง
     */
    @Test
    void TC_UT_10_01_shouldDisplayRepairRequestDetail() {

        UUID requestId = UUID.randomUUID();

        RepairRequest request =
                mock(RepairRequest.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        String view =
                controller.viewDetail(requestId, model);

        assertEquals(
                "admin/repair-request-detail",
                view
        );

        verify(repairRequestService)
                .getById(requestId);

        verify(model)
                .addAttribute(
                        "request",
                        request
                );
    }

    /**
     * TC-UT-10-02
     * ตรวจสอบการดึงข้อมูลคำร้องตาม ID
     */
    @Test
    void TC_UT_10_02_shouldGetRepairRequestById() {

        UUID requestId = UUID.randomUUID();

        RepairRequest request =
                mock(RepairRequest.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        controller.viewDetail(requestId, model);

        verify(repairRequestService)
                .getById(requestId);

        verifyNoMoreInteractions(
                repairRequestService
        );
    }

    /**
     * TC-UT-10-03
     * ตรวจสอบการเพิ่มข้อมูลคำร้องลงใน Model
     */
    @Test
    void TC_UT_10_03_shouldAddRequestToModel() {

        UUID requestId = UUID.randomUUID();

        RepairRequest request =
                mock(RepairRequest.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        controller.viewDetail(requestId, model);

        verify(model)
                .addAttribute(
                        "request",
                        request
                );
    }

    /**
     * TC-UT-10-04
     * ตรวจสอบว่าข้อมูลคำร้องที่ได้จาก Service
     * ถูกส่งเข้า Model เป็น object เดียวกัน
     */
    @Test
    void TC_UT_10_04_shouldPassSameRequestObjectToModel() {

        UUID requestId = UUID.randomUUID();

        RepairRequest request =
                mock(RepairRequest.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        controller.viewDetail(requestId, model);

        ArgumentCaptor<RepairRequest> captor =
                ArgumentCaptor.forClass(
                        RepairRequest.class
                );

        verify(model)
                .addAttribute(
                        eq("request"),
                        captor.capture()
                );

        RepairRequest capturedRequest =
                captor.getValue();

        assertSame(
                request,
                capturedRequest
        );
    }

    /**
     * TC-UT-10-05
     * ตรวจสอบ View ที่ Controller ส่งกลับ
     */
    @Test
    void TC_UT_10_05_shouldReturnRepairRequestDetailView() {

        UUID requestId = UUID.randomUUID();

        RepairRequest request =
                mock(RepairRequest.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        String view =
                controller.viewDetail(
                        requestId,
                        model
                );

        assertEquals(
                "admin/repair-request-detail",
                view
        );

        verify(repairRequestService)
                .getById(requestId);

        verify(model)
                .addAttribute(
                        "request",
                        request
                );
    }
}
