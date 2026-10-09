
package com.example.dormitory.controller.api;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.response.AdminReporterResponse;
import com.example.dormitory.service.AdminReporterService;

@WebMvcTest(AdminReporterApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminReporterApiControllerTS21IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminReporterService adminReporterService;

    private UUID reporterId;
    private AdminReporterResponse reporter;

    @BeforeEach
    void setUp() {
        reporterId = UUID.fromString(
                "11111111-1111-1111-1111-111111111111"
        );

        reporter = mock(AdminReporterResponse.class);
    }

    /**
     * TC-IT-21-01
     * ตรวจสอบการเรียกดูรายการผู้แจ้งเมื่อมีข้อมูล
     */
    @Test
    void shouldReturnReporterListWhenDataExists() throws Exception {

        when(adminReporterService.getAllReporters())
                .thenReturn(List.of(reporter));

        mockMvc.perform(get("/api/admin/reporters"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1));

        verify(adminReporterService, times(1))
                .getAllReporters();
    }

    /**
     * TC-IT-21-02
     * ตรวจสอบการเรียกดูรายการผู้แจ้งเมื่อไม่มีข้อมูล
     */
    @Test
    void shouldReturnEmptyListWhenNoReportersExist() throws Exception {

        when(adminReporterService.getAllReporters())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/admin/reporters"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().json("[]"));

        verify(adminReporterService, times(1))
                .getAllReporters();
    }

    /**
     * TC-IT-21-03
     * ตรวจสอบว่า Controller เรียก Service เพื่อดึงรายการผู้แจ้ง
     */
    @Test
    void shouldCallServiceOnceToGetAllReporters() throws Exception {

        when(adminReporterService.getAllReporters())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/admin/reporters"))
                .andExpect(status().isOk());

        verify(adminReporterService, times(1))
                .getAllReporters();
    }
}
