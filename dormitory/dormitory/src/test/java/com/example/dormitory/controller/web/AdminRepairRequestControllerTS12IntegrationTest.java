package com.example.dormitory.controller.web;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

@WebMvcTest(AdminRepairRequestController.class)
@AutoConfigureMockMvc
class AdminRepairRequestControllerTS12IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    /**
     * เพิ่ม CSRF token เป็น request attribute
     * สำหรับกรณีที่ Thymeleaf ต้องใช้ ${_csrf.token}
     * ระหว่าง render หน้า HTML
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

    // =========================================================
    // TC-IT-12-01
    // ตรวจสอบการเปิดหน้ามอบหมายงานให้ช่าง
    // =========================================================
    @Test
    void shouldOpenAssignTechnicianPage() throws Exception {

        UUID requestId = UUID.randomUUID();

        RepairRequest request = mock(RepairRequest.class);

        List<Technician> technicians = List.of(
                mock(Technician.class),
                mock(Technician.class)
        );

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        when(repairAssignmentService.getAllTechnicians())
                .thenReturn(technicians);

        mockMvc.perform(
                get("/admin/requests/{id}/assign", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/assign-technician"))
        .andExpect(model().attribute("request", request))
        .andExpect(model().attribute("technicians", technicians));

        verify(repairRequestService)
                .getById(requestId);

        verify(repairAssignmentService)
                .getAllTechnicians();
    }

    // =========================================================
    // TC-IT-12-02
    // ตรวจสอบการดึงข้อมูลคำร้องตาม ID
    // =========================================================
    @Test
    void shouldGetRepairRequestById() throws Exception {

        UUID requestId = UUID.randomUUID();

        RepairRequest request = mock(RepairRequest.class);

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        when(repairAssignmentService.getAllTechnicians())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/admin/requests/{id}/assign", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(model().attribute("request", request));

        verify(repairRequestService, times(1))
                .getById(requestId);
    }

    // =========================================================
    // TC-IT-12-03
    // ตรวจสอบการแสดงรายการช่างทั้งหมด
    // =========================================================
    @Test
    void shouldDisplayAllTechnicians() throws Exception {

        UUID requestId = UUID.randomUUID();

        RepairRequest request = mock(RepairRequest.class);

        Technician technician1 = mock(Technician.class);
        Technician technician2 = mock(Technician.class);

        List<Technician> technicians = List.of(
                technician1,
                technician2
        );

        when(repairRequestService.getById(requestId))
                .thenReturn(request);

        when(repairAssignmentService.getAllTechnicians())
                .thenReturn(technicians);

        mockMvc.perform(
                get("/admin/requests/{id}/assign", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(model().attribute("technicians", technicians));

        verify(repairAssignmentService, times(1))
                .getAllTechnicians();
    }

    // =========================================================
    // TC-IT-12-04
    // ตรวจสอบการมอบหมายงานให้ช่าง
    // =========================================================
    @Test
    void shouldAssignTechnicianSuccessfully() throws Exception {

        UUID requestId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        String adminNote = "มอบหมายงานตามประเภทและลักษณะของงาน";

        mockMvc.perform(
                post("/admin/requests/{id}/assign", requestId)
                        .param("technicianId", technicianId.toString())
                        .param("adminNote", adminNote)
                        .sessionAttr("adminId", adminId)
                        .with(csrf())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
                "/admin/requests/" + requestId
        ));

        verify(repairAssignmentService, times(1))
                .assignTechnician(
                        requestId,
                        technicianId,
                        adminId,
                        adminNote
                );
    }

    // =========================================================
    // TC-IT-12-05
    // ตรวจสอบกรณีไม่มี Admin ID ใน Session
    // =========================================================
    @Test
    void shouldRedirectToLoginWhenAdminIdIsMissing()
            throws Exception {

        UUID requestId = UUID.randomUUID();
        UUID technicianId = UUID.randomUUID();

        mockMvc.perform(
                post("/admin/requests/{id}/assign", requestId)
                        .param("technicianId", technicianId.toString())
                        .param("adminNote", "หมายเหตุทดสอบ")
                        .with(csrf())
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));

        verify(repairAssignmentService, never())
                .assignTechnician(
                        any(UUID.class),
                        any(UUID.class),
                        any(UUID.class),
                        anyString()
                );
    }
}
