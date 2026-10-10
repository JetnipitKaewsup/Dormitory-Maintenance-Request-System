package com.example.dormitory.controller.web;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminRepairRequestControllerTS13Test {

    private RepairRequestService repairRequestService;
    private RepairAssignmentService repairAssignmentService;
    private AdminRepairRequestController controller;

    private Model model;
    private HttpSession session;

    private UUID requestId;
    private UUID adminId;

    @BeforeEach
    void setUp() {

        repairRequestService = mock(RepairRequestService.class);
        repairAssignmentService = mock(RepairAssignmentService.class);

        controller = new AdminRepairRequestController(
                repairRequestService,
                repairAssignmentService
        );

        model = mock(Model.class);
        session = mock(HttpSession.class);

        requestId = UUID.randomUUID();
        adminId = UUID.randomUUID();
    }

    // =========================================================
    // TC-UT-13-01
    // ตรวจสอบการเปิดหน้าตรวจสอบงาน
    // =========================================================

    @Test
    @DisplayName("TC-UT-13-01 ตรวจสอบการเปิดหน้าตรวจสอบงาน")
    void TC_UT_13_01_shouldDisplayInspectPage() {

        RepairRequest request = mock(RepairRequest.class);
        RepairAssignment assignment = mock(RepairAssignment.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        when(repairAssignmentService.getAssignmentByRequestId(requestId))
                .thenReturn(assignment);

        String result = controller.showInspectPage(
                requestId,
                model
        );

        assertEquals(
                "admin/inspect-work",
                result
        );

        verify(repairRequestService)
                .getById(requestId);

        verify(repairAssignmentService)
                .getAssignmentByRequestId(requestId);

        verify(model)
                .addAttribute("request", request);

        verify(model)
                .addAttribute("assignment", assignment);
    }

    // =========================================================
    // TC-UT-13-02
    // ตรวจสอบการดึงข้อมูลคำร้องตาม ID
    // =========================================================

    @Test
    @DisplayName("TC-UT-13-02 ตรวจสอบการดึงข้อมูลคำร้องตาม ID")
    void TC_UT_13_02_shouldGetRepairRequestById() {

        RepairRequest request = mock(RepairRequest.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        when(repairAssignmentService.getAssignmentByRequestId(requestId))
                .thenReturn(null);

        controller.showInspectPage(
                requestId,
                model
        );

        verify(repairRequestService, times(1))
                .getById(requestId);

        ArgumentCaptor<Object> captor =
                ArgumentCaptor.forClass(Object.class);

        verify(model)
                .addAttribute(eq("request"), captor.capture());

        assertSame(
                request,
                captor.getValue()
        );
    }

    // =========================================================
    // TC-UT-13-03
    // ตรวจสอบการดึงข้อมูลการมอบหมายงาน
    // =========================================================

    @Test
    @DisplayName("TC-UT-13-03 ตรวจสอบการดึงข้อมูลการมอบหมายงาน")
    void TC_UT_13_03_shouldGetRepairAssignment() {

        RepairRequest request = mock(RepairRequest.class);
        RepairAssignment assignment = mock(RepairAssignment.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        when(repairAssignmentService.getAssignmentByRequestId(requestId))
                .thenReturn(assignment);

        controller.showInspectPage(
                requestId,
                model
        );

        verify(
                repairAssignmentService,
                times(1)
        ).getAssignmentByRequestId(requestId);

        verify(model)
                .addAttribute(
                        "assignment",
                        assignment
                );
    }

    // =========================================================
    // TC-UT-13-04
    // ตรวจสอบการยืนยันงานเสร็จสิ้น
    // =========================================================

    @Test
    @DisplayName("TC-UT-13-04 ตรวจสอบการยืนยันการดำเนินงานเสร็จสิ้น")
    void TC_UT_13_04_shouldConfirmCompletion() {

        when(session.getAttribute("adminId"))
                .thenReturn(adminId);

        String note =
                "ตรวจสอบงานเรียบร้อยแล้ว สามารถปิดงานได้";

        String result = controller.confirmCompletion(
                requestId,
                note,
                session
        );

        assertEquals(
                "redirect:/admin/requests/" + requestId,
                result
        );

        verify(session)
                .getAttribute("adminId");
    }

    // =========================================================
    // TC-UT-13-05
    // ตรวจสอบกรณีไม่มี Admin ID ใน Session
    // =========================================================

    @Test
    @DisplayName("TC-UT-13-05 ตรวจสอบกรณีไม่มี Admin ID ใน Session")
    void TC_UT_13_05_shouldRejectCompletionWithoutAdminSession() {

        when(session.getAttribute("adminId"))
                .thenReturn(null);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> controller.confirmCompletion(
                                requestId,
                                "ตรวจสอบงาน",
                                session
                        )
                );

        assertEquals(
                "ยังไม่ได้ login เป็น admin",
                exception.getMessage()
        );

        verify(session)
                .getAttribute("adminId");

        verifyNoInteractions(
                repairRequestService,
                repairAssignmentService
        );
    }
}
