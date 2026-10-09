package com.example.dormitory.controller.api;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.request.AdminReporterUpdateRequest;
import com.example.dormitory.dto.response.AdminReporterResponse;
import com.example.dormitory.service.AdminReporterService;

@WebMvcTest(AdminReporterApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminReporterApiControllerTS22IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminReporterService adminReporterService;

    private UUID reporterId;

    @BeforeEach
    void setUp() {
        reporterId = UUID.fromString(
                "11111111-1111-1111-1111-111111111111");
    }

    // TC-IT-22-01: แก้ไขข้อมูลผู้แจ้งสำเร็จ
    @Test
    void shouldUpdateReporterSuccessfully() throws Exception {

        String requestJson = """
                {
                    "firstName": "Anan",
                    "lastName": "Sukjai",
                    "phoneNo": "0891234567"
                }
                """;

        AdminReporterResponse response = new AdminReporterResponse(
                reporterId,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Anan",
                "Sukjai",
                "0891234567",
                "anan",
                "anan@example.com",
                1201
        );

        when(adminReporterService.updateReporter(
                eq(reporterId),
                any(AdminReporterUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/admin/reporters/{reporterId}", reporterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reporterId")
                        .value(reporterId.toString()))
                .andExpect(jsonPath("$.firstName").value("Anan"))
                .andExpect(jsonPath("$.lastName").value("Sukjai"))
                .andExpect(jsonPath("$.phoneNo").value("0891234567"))
                .andExpect(jsonPath("$.username").value("anan"))
                .andExpect(jsonPath("$.email").value("anan@example.com"))
                .andExpect(jsonPath("$.roomNo").value(1201));

        verify(adminReporterService, times(1)).updateReporter(
                eq(reporterId),
                any(AdminReporterUpdateRequest.class));
    }

    // TC-IT-22-02: ตรวจสอบว่าข้อมูลจาก Request ถูกส่งเข้า Service
    @Test
    void shouldPassUpdatedFieldsToService() throws Exception {

        String requestJson = """
                {
                    "firstName": "Suda",
                    "lastName": "Dee",
                    "phoneNo": "0812345678"
                }
                """;

        when(adminReporterService.updateReporter(
                eq(reporterId),
                any(AdminReporterUpdateRequest.class)))
                .thenReturn(new AdminReporterResponse(
                        reporterId,
                        null,
                        "Suda",
                        "Dee",
                        "0812345678",
                        null,
                        null,
                        null
                ));

        mockMvc.perform(put("/api/admin/reporters/{reporterId}", reporterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Suda"))
                .andExpect(jsonPath("$.lastName").value("Dee"))
                .andExpect(jsonPath("$.phoneNo").value("0812345678"));

        org.mockito.ArgumentCaptor<AdminReporterUpdateRequest> captor =
                org.mockito.ArgumentCaptor.forClass(
                        AdminReporterUpdateRequest.class);

        verify(adminReporterService).updateReporter(
                eq(reporterId),
                captor.capture());

        org.junit.jupiter.api.Assertions.assertEquals(
                "Suda", captor.getValue().getFirstName());
        org.junit.jupiter.api.Assertions.assertEquals(
                "Dee", captor.getValue().getLastName());
        org.junit.jupiter.api.Assertions.assertEquals(
                "0812345678", captor.getValue().getPhoneNo());
    }


    // TC-IT-22-03: ตรวจสอบการตอบกลับเมื่อไม่พบผู้แจ้ง
    @Test
    void shouldPropagateExceptionWhenReporterDoesNotExist() throws Exception {

        String requestJson = """
                {
                    "firstName": "Anan",
                    "lastName": "Sukjai",
                    "phoneNo": "0891234567"
                }
                """;

        when(adminReporterService.updateReporter(
                eq(reporterId),
                any(AdminReporterUpdateRequest.class)))
                .thenThrow(new RuntimeException("Reporter not found"));

        mockMvc.perform(
                        put("/api/admin/reporters/{reporterId}", reporterId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));

        verify(adminReporterService, times(1)).updateReporter(
                eq(reporterId),
                any(AdminReporterUpdateRequest.class));
    }


    // TC-IT-22-04: ปฏิเสธ JSON ที่มีรูปแบบไม่ถูกต้อง
    @Test
    void shouldRejectMalformedJsonRequest() throws Exception {

        mockMvc.perform(put("/api/admin/reporters/{reporterId}", reporterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":"))
                .andExpect(status().isBadRequest());

        verify(adminReporterService, never()).updateReporter(
                eq(reporterId),
                any(AdminReporterUpdateRequest.class));
    }
}
