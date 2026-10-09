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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.request.AdminTechnicianCreateRequest;
import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.service.AdminTechnicianService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(AdminTechnicianApiController.class)
class AdminTechnicianApiControllerTS18IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // สร้าง ObjectMapper โดยตรง ไม่ต้องพึ่ง Spring Bean
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @MockitoBean
    private AdminTechnicianService adminTechnicianService;

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final UUID userId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    // เตรียมข้อมูลคำขอสร้างบัญชีช่าง
    private String validRequestJson() throws Exception {
        AdminTechnicianCreateRequest request =
                new AdminTechnicianCreateRequest();

        request.setEmail("technician@example.com");
        request.setUsername("tech001");
        request.setPassword("password123");
        request.setFirstName("Somchai");
        request.setLastName("Jaidee");
        request.setPhoneNo("0812345678");
        request.setSpecialization("ไฟฟ้า");

        return objectMapper.writeValueAsString(request);
    }

    // เตรียมผลลัพธ์จำลองจาก Service
    private AdminTechnicianResponse createResponse() {
        return new AdminTechnicianResponse(
                technicianId,
                "ไฟฟ้า",
                userId,
                "Somchai",
                "Jaidee",
                "0812345678",
                "tech001"
        );
    }

    // TC-IT-18-01: ตรวจสอบว่าสร้างบัญชีช่างแล้วตอบ HTTP 201
    @Test
    void TC_IT_18_01_createTechnicianReturns201() throws Exception {
        when(adminTechnicianService.createTechnician(
                any(AdminTechnicianCreateRequest.class)))
                .thenReturn(createResponse());

        mockMvc.perform(post("/api/admin/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated());

        verify(adminTechnicianService, times(1))
                .createTechnician(any(AdminTechnicianCreateRequest.class));
    }

    // TC-IT-18-02: ตรวจสอบข้อมูล JSON ที่ตอบกลับ
    @Test
    void TC_IT_18_02_createTechnicianReturnsCorrectResponse()
            throws Exception {

        when(adminTechnicianService.createTechnician(
                any(AdminTechnicianCreateRequest.class)))
                .thenReturn(createResponse());

        mockMvc.perform(post("/api/admin/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.technicianId")
                        .value(technicianId.toString()))
                .andExpect(jsonPath("$.userId")
                        .value(userId.toString()))
                .andExpect(jsonPath("$.username").value("tech001"))
                .andExpect(jsonPath("$.firstName").value("Somchai"))
                .andExpect(jsonPath("$.lastName").value("Jaidee"))
                .andExpect(jsonPath("$.phoneNo").value("0812345678"))
                .andExpect(jsonPath("$.specialization").value("ไฟฟ้า"));
    }

    // TC-IT-18-03: ตรวจสอบว่าข้อมูลจาก HTTP Request
    // ถูกแปลงเป็น AdminTechnicianCreateRequest อย่างถูกต้อง
    @Test
    void TC_IT_18_03_requestDataIsMappedCorrectly()
            throws Exception {

        when(adminTechnicianService.createTechnician(
                any(AdminTechnicianCreateRequest.class)))
                .thenReturn(createResponse());

        mockMvc.perform(post("/api/admin/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestJson()))
                .andExpect(status().isCreated());

        var captor = forClass(AdminTechnicianCreateRequest.class);

        verify(adminTechnicianService)
                .createTechnician(captor.capture());

        AdminTechnicianCreateRequest captured = captor.getValue();

        assertEquals("technician@example.com", captured.getEmail());
        assertEquals("tech001", captured.getUsername());
        assertEquals("password123", captured.getPassword());
        assertEquals("Somchai", captured.getFirstName());
        assertEquals("Jaidee", captured.getLastName());
        assertEquals("0812345678", captured.getPhoneNo());
        assertEquals("ไฟฟ้า", captured.getSpecialization());
    }
}
