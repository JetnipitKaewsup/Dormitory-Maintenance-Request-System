package com.example.dormitory.controller.web;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
class AdminRepairRequestControllerTS13IntegrationTest {

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

    /**
     * เพิ่ม CSRF token เป็น request attribute
     * สำหรับหน้า Thymeleaf ที่อ้างถึง ${_csrf.token}
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

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        // เตรียมข้อมูลคำร้องสำหรับหน้าตรวจสอบงาน
        repairRequest = mock(RepairRequest.class);

        when(repairRequest.getRepairRequestId())
                .thenReturn(requestId);

        when(repairRequest.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        // เตรียมข้อมูลการมอบหมายงาน
        repairAssignment = mock(RepairAssignment.class);

        when(repairAssignment.getJobStatus())
                .thenReturn(null);

        // Mock Service
        when(repairRequestService.getById(requestId))
                .thenReturn(repairRequest);

        when(repairAssignmentService.getAssignmentByRequestId(requestId))
                .thenReturn(repairAssignment);
    }

    // =============================================================
    // TC-IT-13-01
    // ตรวจสอบการเปิดหน้าตรวจสอบงาน
    // =============================================================
    @Test
    void shouldOpenInspectPage() throws Exception {
        mockMvc.perform(
                get("/admin/requests/{id}/inspect", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/inspect-work"))
        .andExpect(model().attribute("request", repairRequest))
        .andExpect(model().attribute("assignment", repairAssignment));

        verify(repairRequestService)
                .getById(requestId);

        verify(repairAssignmentService)
                .getAssignmentByRequestId(requestId);
    }

    // =============================================================
    // TC-IT-13-02
    // ตรวจสอบการดึงข้อมูลคำร้องตาม ID
    // =============================================================
    @Test
    void shouldGetRepairRequestById() throws Exception {
        mockMvc.perform(
                get("/admin/requests/{id}/inspect", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(model().attribute("request", repairRequest));

        verify(repairRequestService, times(1))
                .getById(requestId);
    }

    // =============================================================
    // TC-IT-13-03
    // ตรวจสอบการดึงข้อมูลการมอบหมายงาน
    // =============================================================
    @Test
    void shouldGetRepairAssignment() throws Exception {
        mockMvc.perform(
                get("/admin/requests/{id}/inspect", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(model().attribute("assignment", repairAssignment));

        verify(repairAssignmentService, times(1))
                .getAssignmentByRequestId(requestId);
    }

    // =============================================================
    // TC-IT-13-04
    // ตรวจสอบการยืนยันการดำเนินงานเสร็จสิ้น
    // =============================================================
    @Test
    void shouldConfirmCompletion() throws Exception {
        mockMvc.perform(
                post("/admin/requests/{id}/inspect", requestId)
                        .sessionAttr("adminId", adminId)
                        .param("note", "ตรวจสอบงานเรียบร้อยแล้ว")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
                "/admin/requests/" + requestId
        ));
    }

    // =============================================================
    // TC-IT-13-05
    // ตรวจสอบกรณีไม่มี Admin ID ใน Session
    // =============================================================
    @Test
    void shouldRedirectToLoginWhenAdminIdMissing() throws Exception {
        mockMvc.perform(
                post("/admin/requests/{id}/inspect", requestId)
                        .param("note", "ตรวจสอบงาน")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"))
        .andExpect(flash().attribute(
                "error",
                "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
        ));

        verifyNoInteractions(repairRequestService);
        verifyNoInteractions(repairAssignmentService);
    }
}
