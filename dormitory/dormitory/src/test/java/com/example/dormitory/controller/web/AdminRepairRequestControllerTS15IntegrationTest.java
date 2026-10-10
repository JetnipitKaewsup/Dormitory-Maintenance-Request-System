
package com.example.dormitory.controller.web;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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

        when(repairRequest.getRepairRequestId()).thenReturn(requestId);
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
     * เพิ่ม CSRF token ใน request attribute
     * เพื่อให้ Thymeleaf สามารถอ่าน ${_csrf.token} ได้
     */
    private RequestPostProcessor withCsrfRequestAttribute() {
        return request -> {
            CsrfToken csrfToken = new DefaultCsrfToken(
                    "X-CSRF-TOKEN",
                    "_csrf",
                    "test-csrf-token"
            );

            request.setAttribute(CsrfToken.class.getName(), csrfToken);
            request.setAttribute("_csrf", csrfToken);

            return request;
        };
    }

    /**
     * TC-IT-15-01
     * ตรวจสอบว่าเปิดหน้าตรวจสอบงานได้
     */
    @Test
    void TC_IT_15_01_adminCanViewInspectWorkPage() throws Exception {
        mockMvc.perform(
                get("/admin/requests/{id}/inspect", requestId)
                        .with(withCsrfRequestAttribute())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inspect-work"))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("assignment"));

        verify(repairRequestService).getById(requestId);
        verify(repairAssignmentService)
                .getAssignmentByRequestId(requestId);
    }

    /**
     * TC-IT-15-02
     * ตรวจสอบการยืนยันงานสำเร็จพร้อมหมายเหตุ
     */
    @Test
    void TC_IT_15_02_adminCanConfirmCompletionWithNote()
            throws Exception {

        String note =
                "โทรประสานผู้แจ้งแล้ว ผู้แจ้งยืนยันว่าซ่อมเรียบร้อย";

        mockMvc.perform(
                post("/admin/requests/{id}/inspect", requestId)
                        .sessionAttr("adminId", adminId)
                        .param("note", note)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/requests/" + requestId
                ));

        verify(repairRequestService).adminUpdateStatus(
                requestId,
                adminId,
                RepairRequestStatus.COMPLETED,
                note
        );
    }

    /**
     * TC-IT-15-03
     * ตรวจสอบการยืนยันงานสำเร็จโดยไม่ระบุหมายเหตุ
     */
    @Test
    void TC_IT_15_03_adminCanConfirmCompletionWithoutNote()
            throws Exception {

        mockMvc.perform(
                post("/admin/requests/{id}/inspect", requestId)
                        .sessionAttr("adminId", adminId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/requests/" + requestId
                ));

        verify(repairRequestService).adminUpdateStatus(
                requestId,
                adminId,
                RepairRequestStatus.COMPLETED,
                null
        );
    }

    /**
     * TC-IT-15-04
     * ตรวจสอบการยืนยันงานเมื่อไม่มี adminId ใน session
     */
    @Test
    void TC_IT_15_04_redirectsToLoginWhenAdminSessionIsMissing()
            throws Exception {

        mockMvc.perform(
                post("/admin/requests/{id}/inspect", requestId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(repairRequestService, never()).adminUpdateStatus(
                org.mockito.ArgumentMatchers.eq(requestId),
                org.mockito.ArgumentMatchers.any(UUID.class),
                org.mockito.ArgumentMatchers.eq(
                        RepairRequestStatus.COMPLETED
                ),
                org.mockito.ArgumentMatchers.any()
        );
    }

    /**
     * TC-IT-15-05
     * ตรวจสอบว่าใช้ adminId จาก session
     * ไม่ใช่ adminId ที่ส่งมาจาก request parameter
     */
    @Test
    void TC_IT_15_05_usesAdminIdFromSession() throws Exception {
        UUID spoofedAdminId = UUID.randomUUID();
        String note = "ประสานงานกับผู้แจ้งแล้ว";

        mockMvc.perform(
                post("/admin/requests/{id}/inspect", requestId)
                        .sessionAttr("adminId", adminId)
                        .param("adminId", spoofedAdminId.toString())
                        .param("note", note)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/requests/" + requestId
                ));

        verify(repairRequestService).adminUpdateStatus(
                requestId,
                adminId,
                RepairRequestStatus.COMPLETED,
                note
        );
    }
}
