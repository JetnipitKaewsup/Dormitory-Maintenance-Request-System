package com.example.dormitory.controller.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.response.RepairRequestHistoryResponse;
import com.example.dormitory.service.AdminReporterService;

@WebMvcTest(AdminReporterApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminReporterApiControllerTS23IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminReporterService adminReporterService;

    private UUID reporterId;
    private UUID requestId;

    @BeforeEach
    void setUp() {
        reporterId = UUID.fromString(
                "11111111-1111-1111-1111-111111111111");

        requestId = UUID.fromString(
                "22222222-2222-2222-2222-222222222222");
    }

    // TC-IT-23-01:
    // ตรวจสอบการดึงประวัติการแจ้งซ่อมสำเร็จ
    @Test
    void shouldReturnRepairHistorySuccessfully() throws Exception {

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 10, 1, 10, 30);

        LocalDateTime startDateTime =
                LocalDateTime.of(2026, 10, 2, 9, 0);

        LocalDateTime endDateTime =
                LocalDateTime.of(2026, 10, 2, 11, 0);

        RepairRequestHistoryResponse response =
                new RepairRequestHistoryResponse(
                        requestId,
                        "PLUMBING",
                        "COMPLETED",
                        "ทดสอบประวัติการแจ้งซ่อม",
                        createdAt,
                        startDateTime,
                        endDateTime
                );

        when(adminReporterService.getRepairHistoryByReporterId(reporterId))
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/admin/reporters/{reporterId}/history",
                                reporterId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].repairRequestId")
                        .value(requestId.toString()))
                .andExpect(jsonPath("$[0].repairType").value("PLUMBING"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$[0].description")
                        .value("ทดสอบประวัติการแจ้งซ่อม"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-10-01T10:30:00"))
                .andExpect(jsonPath("$[0].startDateTime")
                        .value("2026-10-02T09:00:00"))
                .andExpect(jsonPath("$[0].endDateTime")
                        .value("2026-10-02T11:00:00"));

        verify(adminReporterService, times(1))
                .getRepairHistoryByReporterId(reporterId);
    }

    // TC-IT-23-02:
    // ตรวจสอบกรณีผู้แจ้งไม่มีประวัติการแจ้งซ่อม
    @Test
    void shouldReturnEmptyListWhenNoHistoryExists() throws Exception {

        when(adminReporterService.getRepairHistoryByReporterId(reporterId))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/admin/reporters/{reporterId}/history",
                                reporterId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(adminReporterService, times(1))
                .getRepairHistoryByReporterId(reporterId);
    }

    // TC-IT-23-03:
    // ตรวจสอบกรณี Service แจ้งว่าไม่พบผู้แจ้ง
    @Test
    void shouldReturnInternalServerErrorWhenReporterDoesNotExist()
            throws Exception {

        when(adminReporterService.getRepairHistoryByReporterId(reporterId))
                .thenThrow(new RuntimeException("Reporter not found"));

        mockMvc.perform(
                        get("/api/admin/reporters/{reporterId}/history",
                                reporterId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));

        verify(adminReporterService, times(1))
                .getRepairHistoryByReporterId(reporterId);
    }


    // TC-IT-23-04:
    // ตรวจสอบกรณี reporterId ใน URL ไม่ใช่ UUID ที่ถูกต้อง
    @Test
    void shouldReturnInternalServerErrorWhenReporterIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/api/admin/reporters/{reporterId}/history",
                                "invalid-uuid"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));

        verify(adminReporterService, never())
                .getRepairHistoryByReporterId(any(UUID.class));
    }
}
