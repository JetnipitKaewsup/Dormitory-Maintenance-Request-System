package com.example.dormitory.controller.web;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

@WebMvcTest(AdminRepairRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminRepairRequestControllerTS15IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    private UUID requestId;
    private UUID adminId;

    private RepairRequest repairRequest;
    private RepairAssignment repairAssignment;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        repairRequest = mock(RepairRequest.class);
        repairAssignment = mock(RepairAssignment.class);

        when(repairRequest.getRepairRequestId())
                .thenReturn(requestId);

        when(repairRequest.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(repairAssignment.getJobStatus())
                .thenReturn(RepairRequestStatus.COMPLETED);

        when(repairRequestService.getById(requestId))
                .thenReturn(repairRequest);

        when(repairAssignmentService.getAssignmentByRequestId(requestId))
                .thenReturn(repairAssignment);
    }

    /**
     * TC-IT-15-01
     * ตรวจสอบว่าผู้ดูแลระบบเปิดหน้าตรวจสอบงานได้
     */
    @Test
    void shouldOpenInspectWorkPage() throws Exception {

        mockMvc.perform(
                        get("/admin/requests/{id}/inspect", requestId)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inspect-work"))
                .andExpect(model().attribute("request", repairRequest))
                .andExpect(model().attribute(
                        "assignment", repairAssignment));

        verify(repairRequestService)
                .getById(requestId);

        verify(repairAssignmentService)
                .getAssignmentByRequestId(requestId);
    }

    /**
     * TC-IT-15-02
     * ตรวจสอบว่าการยืนยันผลซ่อมส่งสถานะ COMPLETED
     * และหมายเหตุไปยัง RepairRequestService
     */
    @Test
    void shouldConfirmCompletionWithReporterCoordinationNote()
            throws Exception {

        String note = "โทรประสานผู้แจ้งแล้ว ผู้แจ้งยืนยันว่าซ่อมเรียบร้อย";

        mockMvc.perform(
                        post("/admin/requests/{id}/inspect", requestId)
                                .sessionAttr("adminId", adminId)
                                .param("note", note)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/requests/" + requestId));

        verify(repairRequestService, times(1))
                .adminUpdateStatus(
                        requestId,
                        adminId,
                        RepairRequestStatus.COMPLETED,
                        note
                );
    }

    /**
     * TC-IT-15-03
     * ตรวจสอบว่าระบบรองรับการยืนยันโดยไม่ระบุหมายเหตุ
     */
    @Test
    void shouldConfirmCompletionWithoutNote() throws Exception {

        mockMvc.perform(
                        post("/admin/requests/{id}/inspect", requestId)
                                .sessionAttr("adminId", adminId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/requests/" + requestId));

        verify(repairRequestService, times(1))
                .adminUpdateStatus(
                        requestId,
                        adminId,
                        RepairRequestStatus.COMPLETED,
                        null
                );
    }

    /**
     * TC-IT-15-04
     * ตรวจสอบว่าระบบไม่ยืนยันผลซ่อมเมื่อไม่มี adminId ใน session
     */
    @Test
    void shouldRedirectToLoginWhenAdminSessionIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/admin/requests/{id}/inspect", requestId)
                                .param("note", "ตรวจสอบแล้ว")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(repairRequestService, never())
                .adminUpdateStatus(
                        any(UUID.class),
                        any(UUID.class),
                        any(RepairRequestStatus.class),
                        any()
                );
    }

    /**
     * TC-IT-15-05
     * ตรวจสอบว่าการยืนยันใช้ adminId จาก session
     * ไม่ใช่ UUID ที่ส่งผ่าน request parameter
     */
    @Test
    void shouldUseAdminIdFromSession() throws Exception {

        UUID anotherAdminId = UUID.randomUUID();

        mockMvc.perform(
                        post("/admin/requests/{id}/inspect", requestId)
                                .sessionAttr("adminId", adminId)
                                .param("adminId", anotherAdminId.toString())
                                .param("note", "ประสานงานกับผู้แจ้งแล้ว")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/requests/" + requestId));

        verify(repairRequestService, times(1))
                .adminUpdateStatus(
                        requestId,
                        adminId,
                        RepairRequestStatus.COMPLETED,
                        "ประสานงานกับผู้แจ้งแล้ว"
                );
    }
}
