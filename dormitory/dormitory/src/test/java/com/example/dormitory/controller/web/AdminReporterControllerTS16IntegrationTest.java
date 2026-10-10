
package com.example.dormitory.controller.web;

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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.dormitory.dto.response.AdminReporterResponse;
import com.example.dormitory.dto.response.RepairRequestHistoryResponse;
import com.example.dormitory.service.AdminReporterService;

@WebMvcTest(AdminReporterWebController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminReporterControllerTS16IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminReporterService adminReporterService;

    private UUID reporterId;
    private AdminReporterResponse reporter;
    private RepairRequestHistoryResponse history1;
    private RepairRequestHistoryResponse history2;

    @BeforeEach
    void setUp() {
        reporterId = UUID.randomUUID();

        reporter = mock(AdminReporterResponse.class);
        history1 = mock(RepairRequestHistoryResponse.class);
        history2 = mock(RepairRequestHistoryResponse.class);

        when(adminReporterService.getReporterById(reporterId))
                .thenReturn(reporter);

        when(adminReporterService.getRepairHistoryByReporterId(reporterId))
                .thenReturn(List.of(history1));
    }

    /**
     * เพิ่ม CSRF token ใน request attribute
     * เพื่อรองรับ Thymeleaf template ที่อ้างถึง ${_csrf.token}
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
     * TC-IT-16-01
     * ตรวจสอบการเปิดหน้าประวัติการแจ้งซ่อม
     */
    @Test
    void shouldOpenRepairHistoryPage() throws Exception {
        mockMvc.perform(
                get("/admin/reporters/{reporterId}/history", reporterId)
                        .with(withCsrfRequestAttribute())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reporterHistory"))
                .andExpect(model().attribute("reporter", reporter))
                .andExpect(model().attribute("history", List.of(history1)));

        verify(adminReporterService, times(1))
                .getReporterById(reporterId);

        verify(adminReporterService, times(1))
                .getRepairHistoryByReporterId(reporterId);
    }

    /**
     * TC-IT-16-02
     * ตรวจสอบการแสดงประวัติการแจ้งซ่อมหลายรายการ
     */
    @Test
    void shouldDisplayMultipleRepairHistoryRecords() throws Exception {
        List<RepairRequestHistoryResponse> histories =
                List.of(history1, history2);

        when(adminReporterService.getRepairHistoryByReporterId(reporterId))
                .thenReturn(histories);

        mockMvc.perform(
                get("/admin/reporters/{reporterId}/history", reporterId)
                        .with(withCsrfRequestAttribute())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reporterHistory"))
                .andExpect(model().attribute("history", histories));

        verify(adminReporterService)
                .getRepairHistoryByReporterId(reporterId);
    }

    /**
     * TC-IT-16-03
     * ตรวจสอบกรณีผู้แจ้งซ่อมไม่มีประวัติการแจ้งซ่อม
     */
    @Test
    void shouldDisplayPageWhenRepairHistoryIsEmpty() throws Exception {
        when(adminReporterService.getRepairHistoryByReporterId(reporterId))
                .thenReturn(List.of());

        mockMvc.perform(
                get("/admin/reporters/{reporterId}/history", reporterId)
                        .with(withCsrfRequestAttribute())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reporterHistory"))
                .andExpect(model().attribute("reporter", reporter))
                .andExpect(model().attribute("history", List.of()));

        verify(adminReporterService)
                .getRepairHistoryByReporterId(reporterId);
    }

    /**
     * TC-IT-16-04
     * ตรวจสอบการส่งข้อมูลผู้แจ้งซ่อมไปยัง Model
     */
    @Test
    void shouldAddReporterInformationToModel() throws Exception {
        mockMvc.perform(
                get("/admin/reporters/{reporterId}/history", reporterId)
                        .with(withCsrfRequestAttribute())
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("reporter"))
                .andExpect(model().attribute("reporter", reporter));

        verify(adminReporterService, times(1))
                .getReporterById(reporterId);
    }

    /**
     * TC-IT-16-05
     * ตรวจสอบว่า Controller เรียก Service ด้วย reporterId จาก URL
     */
    @Test
    void shouldPassReporterIdToBothServiceMethods() throws Exception {
        mockMvc.perform(
                get("/admin/reporters/{reporterId}/history", reporterId)
                        .with(withCsrfRequestAttribute())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reporterHistory"));

        verify(adminReporterService, times(1))
                .getReporterById(reporterId);

        verify(adminReporterService, times(1))
                .getRepairHistoryByReporterId(reporterId);
    }
}
