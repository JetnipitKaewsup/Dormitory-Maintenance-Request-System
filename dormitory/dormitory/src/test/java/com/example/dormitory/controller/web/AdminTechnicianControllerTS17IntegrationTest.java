
package com.example.dormitory.controller.web;

import java.util.List;

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

import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.service.AdminTechnicianService;

@WebMvcTest(AdminTechnicianWebController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminTechnicianControllerTS17IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminTechnicianService adminTechnicianService;

    private AdminTechnicianResponse technician1;
    private AdminTechnicianResponse technician2;

    @BeforeEach
    void setUp() {
        technician1 = mock(AdminTechnicianResponse.class);
        technician2 = mock(AdminTechnicianResponse.class);

        when(adminTechnicianService.getAllTechnicians())
                .thenReturn(List.of(technician1, technician2));
    }

    /**
     * เพิ่ม CSRF token เป็น request attribute
     * เพื่อให้ Thymeleaf อ่าน ${_csrf.token} ได้
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
     * TC-IT-17-01
     * ตรวจสอบการเปิดหน้ารายการช่างเมื่อมีข้อมูล
     */
    @Test
    void shouldOpenTechnicianListPage() throws Exception {
        mockMvc.perform(
                    get("/admin/technicians")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technicianManage"))
                .andExpect(model().attributeExists("technicians"))
                .andExpect(model().attribute(
                        "technicians",
                        List.of(technician1, technician2)
                ));

        verify(adminTechnicianService, times(1))
                .getAllTechnicians();
    }

    /**
     * TC-IT-17-02
     * ตรวจสอบการแสดงหน้ารายการช่างเมื่อไม่มีข้อมูล
     */
    @Test
    void shouldDisplayPageWhenTechnicianListIsEmpty() throws Exception {
        when(adminTechnicianService.getAllTechnicians())
                .thenReturn(List.of());

        mockMvc.perform(
                    get("/admin/technicians")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technicianManage"))
                .andExpect(model().attributeExists("technicians"))
                .andExpect(model().attribute("technicians", List.of()));

        verify(adminTechnicianService, times(1))
                .getAllTechnicians();
    }

    /**
     * TC-IT-17-03
     * ตรวจสอบว่ารายการช่างถูกส่งไปยัง Model ด้วยชื่อ technicians
     */
    @Test
    void shouldAddTechniciansToModel() throws Exception {
        mockMvc.perform(
                    get("/admin/technicians")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("technicians"))
                .andExpect(model().attribute(
                        "technicians",
                        List.of(technician1, technician2)
                ));

        verify(adminTechnicianService, times(1))
                .getAllTechnicians();
    }

    /**
     * TC-IT-17-04
     * ตรวจสอบว่า Controller เรียก Service เพื่อดึงรายการช่าง
     */
    @Test
    void shouldCallServiceToRetrieveTechnicians() throws Exception {
        mockMvc.perform(
                    get("/admin/technicians")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technicianManage"));

        verify(adminTechnicianService, times(1))
                .getAllTechnicians();
    }
}
