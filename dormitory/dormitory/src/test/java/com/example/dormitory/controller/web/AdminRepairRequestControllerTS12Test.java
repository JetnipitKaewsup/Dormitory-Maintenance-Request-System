package com.example.dormitory.controller.web;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminRepairRequestControllerTS12Test {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private RepairAssignmentService repairAssignmentService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AdminRepairRequestController controller;

    private UUID repairRequestId;
    private UUID technicianId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        repairRequestId = UUID.randomUUID();
        technicianId = UUID.randomUUID();
        adminId = UUID.randomUUID();
    }

    // =========================================================
    // TC-UT-12-01
    // ตรวจสอบการเปิดหน้ามอบหมายงานให้ช่าง
    // =========================================================
    @Test
    void shouldDisplayAssignTechnicianPage() {

        RepairRequest request = mock(RepairRequest.class);
        List<Technician> technicians = List.of(
                mock(Technician.class),
                mock(Technician.class)
        );

        when(repairRequestService.getById(repairRequestId))
                .thenReturn(request);

        when(repairAssignmentService.getAllTechnicians())
                .thenReturn(technicians);

        String view = controller.showAssignPage(
                repairRequestId,
                model
        );

        assertEquals(
                "admin/assign-technician",
                view
        );

        verify(model).addAttribute(
                "request",
                request
        );

        verify(model).addAttribute(
                "technicians",
                technicians
        );
    }

    // =========================================================
    // TC-UT-12-02
    // ตรวจสอบการดึงข้อมูลคำร้องตาม ID
    // =========================================================
    @Test
    void shouldGetRepairRequestById() {

        RepairRequest request = mock(RepairRequest.class);

        when(repairRequestService.getById(repairRequestId))
                .thenReturn(request);

        when(repairAssignmentService.getAllTechnicians())
                .thenReturn(List.of());

        controller.showAssignPage(
                repairRequestId,
                model
        );

        verify(repairRequestService, times(1))
                .getById(repairRequestId);
    }

    // =========================================================
    // TC-UT-12-03
    // ตรวจสอบการดึงรายการช่างทั้งหมด
    // =========================================================
    @Test
    void shouldGetAllTechnicians() {

        RepairRequest request = mock(RepairRequest.class);

        List<Technician> technicians = List.of(
                mock(Technician.class),
                mock(Technician.class)
        );

        when(repairRequestService.getById(repairRequestId))
                .thenReturn(request);

        when(repairAssignmentService.getAllTechnicians())
                .thenReturn(technicians);

        controller.showAssignPage(
                repairRequestId,
                model
        );

        verify(repairAssignmentService, times(1))
                .getAllTechnicians();

        verify(model).addAttribute(
                "technicians",
                technicians
        );
    }

    // =========================================================
    // TC-UT-12-04
    // ตรวจสอบการมอบหมายงานให้ช่าง
    // พร้อม Admin ID และหมายเหตุ
    // =========================================================
    @Test
    void shouldAssignTechnicianSuccessfully() {

        String adminNote = "มอบหมายงานตามประเภทงานไฟฟ้า";

        when(session.getAttribute("adminId"))
                .thenReturn(adminId);

        String view = controller.submitAssign(
                repairRequestId,
                technicianId,
                adminNote,
                session
        );

        assertEquals(
                "redirect:/admin/requests/" + repairRequestId,
                view
        );

        verify(repairAssignmentService, times(1))
                .assignTechnician(
                        repairRequestId,
                        technicianId,
                        adminId,
                        adminNote
                );
    }

    // =========================================================
    // TC-UT-12-05
    // ตรวจสอบกรณีไม่มี Admin ID ใน Session
    // =========================================================
    @Test
    void shouldRejectAssignmentWhenAdminIdIsMissing() {

        when(session.getAttribute("adminId"))
                .thenReturn(null);

        assertThrows(
                IllegalStateException.class,
                () -> controller.submitAssign(
                        repairRequestId,
                        technicianId,
                        "หมายเหตุทดสอบ",
                        session
                )
        );

        verify(
                repairAssignmentService,
                never()
        ).assignTechnician(
                any(UUID.class),
                any(UUID.class),
                any(UUID.class),
                anyString()
        );
    }
}
