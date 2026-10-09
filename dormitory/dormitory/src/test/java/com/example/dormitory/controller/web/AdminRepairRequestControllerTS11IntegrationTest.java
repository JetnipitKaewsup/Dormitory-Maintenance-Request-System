package com.example.dormitory.controller.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

/**
 * TS-11
 * ตรวจสอบและพิจารณาคำร้อง
 *
 * Integration Test สำหรับ AdminRepairRequestController
 */
@WebMvcTest(AdminRepairRequestController.class)
@AutoConfigureMockMvc
class AdminRepairRequestControllerTS11IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    private UUID requestId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        requestId = UUID.randomUUID();
        adminId = UUID.randomUUID();
    }

    /**
     * TC-IT-11-01
     * ตรวจสอบการอนุมัติคำร้อง
     */
    @Test
    void TC_IT_11_01_shouldApproveRepairRequest()
            throws Exception {

        mockMvc.perform(
                post("/admin/requests/{id}/approve", requestId)
                        .sessionAttr("adminId", adminId)
                        .with(
                                org.springframework.security.test
                                        .web.servlet.request.SecurityMockMvcRequestPostProcessors
                                        .csrf()
                        )
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(
                redirectedUrl(
                        "/admin/requests/" + requestId
                )
        );

        verify(repairRequestService)
                .approve(requestId, adminId);
    }

    /**
     * TC-IT-11-02
     * ตรวจสอบการส่ง Admin ID เมื่ออนุมัติ
     */
    @Test
    void TC_IT_11_02_shouldPassAdminIdWhenApproving()
            throws Exception {

        mockMvc.perform(
                post("/admin/requests/{id}/approve", requestId)
                        .sessionAttr("adminId", adminId)
                        .with(
                                org.springframework.security.test
                                        .web.servlet.request.SecurityMockMvcRequestPostProcessors
                                        .csrf()
                        )
        )
        .andExpect(status().is3xxRedirection());

        verify(repairRequestService)
                .approve(requestId, adminId);
    }

    /**
     * TC-IT-11-03
     * ตรวจสอบการปฏิเสธคำร้องพร้อมเหตุผล
     */
    @Test
    void TC_IT_11_03_shouldRejectRepairRequestWithReason()
            throws Exception {

        String reason = "ข้อมูลคำร้องไม่ครบถ้วน";

        mockMvc.perform(
                post("/admin/requests/{id}/reject", requestId)
                        .param("reason", reason)
                        .sessionAttr("adminId", adminId)
                        .with(
                                org.springframework.security.test
                                        .web.servlet.request.SecurityMockMvcRequestPostProcessors
                                        .csrf()
                        )
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(
                redirectedUrl(
                        "/admin/requests/" + requestId
                )
        );

        verify(repairRequestService)
                .reject(
                        requestId,
                        adminId,
                        reason
                );
    }

    /**
     * TC-IT-11-04
     * ตรวจสอบการปฏิเสธคำร้องโดยไม่ระบุเหตุผล
     */
    @Test
    void TC_IT_11_04_shouldRejectRepairRequestWithoutReason()
            throws Exception {

        mockMvc.perform(
                post("/admin/requests/{id}/reject", requestId)
                        .sessionAttr("adminId", adminId)
                        .with(
                                org.springframework.security.test
                                        .web.servlet.request.SecurityMockMvcRequestPostProcessors
                                        .csrf()
                        )
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(
                redirectedUrl(
                        "/admin/requests/" + requestId
                )
        );

        verify(repairRequestService)
                .reject(
                        requestId,
                        adminId,
                        null
                );
    }

    /**
     * TC-IT-11-05
     * ตรวจสอบกรณีไม่มี Admin ID ใน Session
     */
    @Test
    void TC_IT_11_05_shouldRedirectToLoginWhenAdminIdIsMissing()
            throws Exception {

        mockMvc.perform(
                post("/admin/requests/{id}/approve", requestId)
                        .with(
                                org.springframework.security.test
                                        .web.servlet.request.SecurityMockMvcRequestPostProcessors
                                        .csrf()
                        )
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(
                redirectedUrl("/login")
        );
    }
}