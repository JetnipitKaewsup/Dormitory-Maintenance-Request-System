package com.example.dormitory.controller.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

import jakarta.servlet.http.HttpSession;

/**
 * TS-11
 * ตรวจสอบและพิจารณาคำร้อง
 *
 * Unit Test สำหรับ AdminRepairRequestController
 */
class AdminRepairRequestControllerTS11Test {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private RepairAssignmentService repairAssignmentService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AdminRepairRequestController controller;

    private UUID requestId;
    private UUID adminId;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        requestId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        when(session.getAttribute("adminId"))
                .thenReturn(adminId);
    }

    /**
     * TC-UT-11-01
     * ตรวจสอบการอนุมัติคำร้อง
     */
    @Test
    void TC_UT_11_01_shouldApproveRepairRequest() {

        String result =
                controller.approve(requestId, session);

        verify(repairRequestService)
                .approve(requestId, adminId);

        assertEquals(
                "redirect:/admin/requests/" + requestId,
                result
        );
    }

    /**
     * TC-UT-11-02
     * ตรวจสอบการส่ง Admin ID ไปยัง Service เมื่ออนุมัติ
     */
    @Test
    void TC_UT_11_02_shouldPassAdminIdWhenApproving() {

        controller.approve(requestId, session);

        verify(repairRequestService)
                .approve(requestId, adminId);
    }

    /**
     * TC-UT-11-03
     * ตรวจสอบการปฏิเสธคำร้องพร้อมเหตุผล
     */
    @Test
    void TC_UT_11_03_shouldRejectRepairRequestWithReason() {

        String reason = "ข้อมูลคำร้องไม่ครบถ้วน";

        String result =
                controller.reject(
                        requestId,
                        reason,
                        session
                );

        verify(repairRequestService)
                .reject(
                        requestId,
                        adminId,
                        reason
                );

        assertEquals(
                "redirect:/admin/requests/" + requestId,
                result
        );
    }

    /**
     * TC-UT-11-04
     * ตรวจสอบการปฏิเสธคำร้องโดยไม่ระบุเหตุผล
     */
    @Test
    void TC_UT_11_04_shouldRejectRepairRequestWithoutReason() {

        String result =
                controller.reject(
                        requestId,
                        null,
                        session
                );

        verify(repairRequestService)
                .reject(
                        requestId,
                        adminId,
                        null
                );

        assertEquals(
                "redirect:/admin/requests/" + requestId,
                result
        );
    }

    /**
     * TC-UT-11-05
     * ตรวจสอบกรณีไม่มี Admin ID ใน Session
     */
    @Test
    void TC_UT_11_05_shouldThrowExceptionWhenAdminIsNotLoggedIn() {

        when(session.getAttribute("adminId"))
                .thenReturn(null);

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> controller.approve(
                        requestId,
                        session
                )
        );
    }
}