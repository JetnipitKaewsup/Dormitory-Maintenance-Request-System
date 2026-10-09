
package com.example.dormitory.controller.api;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.request.AdminTechnicianUpdateRequest;
import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.service.AdminTechnicianService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(AdminTechnicianApiController.class)
class AdminTechnicianApiControllerTS19IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @MockitoBean
    private AdminTechnicianService adminTechnicianService;

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final UUID userId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    // เตรียม JSON สำหรับคำขอแก้ไขข้อมูลช่าง
    private String validRequestJson() throws Exception {
        AdminTechnicianUpdateRequest request =
                new AdminTechnicianUpdateRequest();

        request.setFirstName("SomchaiUpdated");
        request.setLastName("JaideeUpdated");
        request.setPhoneNo("0898765432");
        request.setSpecialization("ElectricalUpdated");

        return objectMapper.writeValueAsString(request);
    }

    // จำลองผลลัพธ์หลังแก้ไขข้อมูลช่างสำเร็จ
    private AdminTechnicianResponse updatedResponse() {
        return new AdminTechnicianResponse(
                technicianId,
                "ElectricalUpdated",
                userId,
                "SomchaiUpdated",
                "JaideeUpdated",
                "0898765432",
                "tech001"
        );
    }

    // TC-IT-19-01: ตรวจสอบว่าการแก้ไขข้อมูลช่างตอบกลับ HTTP 200
    @Test
    void TC_IT_19_01_updateTechnicianReturns200() throws Exception {

        when(adminTechnicianService.updateTechnician(
                org.mockito.ArgumentMatchers.eq(technicianId),
                any(AdminTechnicianUpdateRequest.class)))
                .thenReturn(updatedResponse());

        mockMvc.perform(put("/api/admin/technicians/{technicianId}", technicianId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isOk());

        verify(adminTechnicianService, times(1))
                .updateTechnician(
                        org.mockito.ArgumentMatchers.eq(technicianId),
                        any(AdminTechnicianUpdateRequest.class));
    }

    // TC-IT-19-02: ตรวจสอบความถูกต้องของ JSON ที่ตอบกลับ
    @Test
    void TC_IT_19_02_updateTechnicianReturnsCorrectResponse()
            throws Exception {

        when(adminTechnicianService.updateTechnician(
                org.mockito.ArgumentMatchers.eq(technicianId),
                any(AdminTechnicianUpdateRequest.class)))
                .thenReturn(updatedResponse());

        mockMvc.perform(put("/api/admin/technicians/{technicianId}", technicianId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.technicianId")
                        .value(technicianId.toString()))
                .andExpect(jsonPath("$.userId")
                        .value(userId.toString()))
                .andExpect(jsonPath("$.username").value("tech001"))
                .andExpect(jsonPath("$.firstName").value("SomchaiUpdated"))
                .andExpect(jsonPath("$.lastName").value("JaideeUpdated"))
                .andExpect(jsonPath("$.phoneNo").value("0898765432"))
                .andExpect(jsonPath("$.specialization")
                        .value("ElectricalUpdated"));
    }

    // TC-IT-19-03: ตรวจสอบว่า JSON ถูกแปลงเป็น UpdateRequest ถูกต้อง
    @Test
    void TC_IT_19_03_requestDataIsMappedCorrectly()
            throws Exception {

        when(adminTechnicianService.updateTechnician(
                org.mockito.ArgumentMatchers.eq(technicianId),
                any(AdminTechnicianUpdateRequest.class)))
                .thenReturn(updatedResponse());

        mockMvc.perform(put("/api/admin/technicians/{technicianId}", technicianId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isOk());

        var captor = forClass(AdminTechnicianUpdateRequest.class);

        verify(adminTechnicianService)
                .updateTechnician(
                        org.mockito.ArgumentMatchers.eq(technicianId),
                        captor.capture());

        AdminTechnicianUpdateRequest captured = captor.getValue();

        assertEquals("SomchaiUpdated", captured.getFirstName());
        assertEquals("JaideeUpdated", captured.getLastName());
        assertEquals("0898765432", captured.getPhoneNo());
        assertEquals("ElectricalUpdated", captured.getSpecialization());
    }

    // TC-IT-19-04: ตรวจสอบว่า Controller ส่ง technicianId ถูกต้องให้ Service
    @Test
    void TC_IT_19_04_controllerPassesCorrectTechnicianId()
            throws Exception {

        when(adminTechnicianService.updateTechnician(
                org.mockito.ArgumentMatchers.eq(technicianId),
                any(AdminTechnicianUpdateRequest.class)))
                .thenReturn(updatedResponse());

        mockMvc.perform(put("/api/admin/technicians/{technicianId}", technicianId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isOk());

        var idCaptor = forClass(UUID.class);

        verify(adminTechnicianService)
                .updateTechnician(
                        idCaptor.capture(),
                        any(AdminTechnicianUpdateRequest.class));

        assertEquals(technicianId, idCaptor.getValue());
    }
}
