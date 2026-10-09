
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.dormitory.dto.response.AdminTechnicianHistoryResponse;
import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.service.AdminTechnicianService;

@WebMvcTest(AdminTechnicianWebController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminTechnicianWebControllerTS20IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminTechnicianService adminTechnicianService;

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private AdminTechnicianResponse technician;
    private AdminTechnicianHistoryResponse historyItem;

    @BeforeEach
    void setUp() {
        // เตรียมข้อมูลช่างและประวัติการซ่อมจำลอง
        technician = mock(AdminTechnicianResponse.class);
        historyItem = mock(AdminTechnicianHistoryResponse.class);

        when(adminTechnicianService.getTechnicianById(technicianId))
                .thenReturn(technician);

        when(adminTechnicianService.getRepairHistoryByTechnicianId(technicianId))
                .thenReturn(List.of(historyItem));
    }

    /**
     * TC-IT-20-01
     * ตรวจสอบการเปิดหน้าประวัติการซ่อมเมื่อช่างมีประวัติ
     */
    @Test
    void shouldOpenRepairHistoryPageWhenHistoryExists() throws Exception {

        mockMvc.perform(get(
                        "/admin/technicians/{technicianId}/history",
                        technicianId
                ))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technicianHistory"))
                .andExpect(model().attributeExists("technician"))
                .andExpect(model().attributeExists("history"))
                .andExpect(model().attribute("technician", technician))
                .andExpect(model().attribute("history", List.of(historyItem)));

        verify(adminTechnicianService, times(1))
                .getTechnicianById(technicianId);

        verify(adminTechnicianService, times(1))
                .getRepairHistoryByTechnicianId(technicianId);
    }

    /**
     * TC-IT-20-02
     * ตรวจสอบการเปิดหน้าประวัติเมื่อช่างไม่มีประวัติการซ่อม
     */
    @Test
    void shouldDisplayPageWhenRepairHistoryIsEmpty() throws Exception {

        when(adminTechnicianService.getRepairHistoryByTechnicianId(technicianId))
                .thenReturn(List.of());

        mockMvc.perform(get(
                        "/admin/technicians/{technicianId}/history",
                        technicianId
                ))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technicianHistory"))
                .andExpect(model().attributeExists("technician"))
                .andExpect(model().attributeExists("history"))
                .andExpect(model().attribute("history", List.of()));

        verify(adminTechnicianService, times(1))
                .getTechnicianById(technicianId);

        verify(adminTechnicianService, times(1))
                .getRepairHistoryByTechnicianId(technicianId);
    }

    /**
     * TC-IT-20-03
     * ตรวจสอบว่า Controller ส่งข้อมูลช่างและประวัติไปยัง Model
     */
    @Test
    void shouldAddTechnicianAndHistoryToModel() throws Exception {

        mockMvc.perform(get(
                        "/admin/technicians/{technicianId}/history",
                        technicianId
                ))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("technician", "history"))
                .andExpect(model().attribute("technician", technician))
                .andExpect(model().attribute("history", List.of(historyItem)));

        verify(adminTechnicianService, times(1))
                .getTechnicianById(technicianId);

        verify(adminTechnicianService, times(1))
                .getRepairHistoryByTechnicianId(technicianId);
    }

    /**
     * TC-IT-20-04
     * ตรวจสอบว่า Controller เรียก Service ด้วยรหัสช่างที่ถูกต้อง
     */
    @Test
    void shouldCallServiceWithCorrectTechnicianId() throws Exception {

        mockMvc.perform(get(
                        "/admin/technicians/{technicianId}/history",
                        technicianId
                ))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/technicianHistory"));

        verify(adminTechnicianService, times(1))
                .getTechnicianById(technicianId);

        verify(adminTechnicianService, times(1))
                .getRepairHistoryByTechnicianId(technicianId);
    }
}
