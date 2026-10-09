
package com.example.dormitory.controller.api;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.request.AdminResidentCreateRequest;
import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.service.AdminResidentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@WebMvcTest(AdminResidentApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminResidentApiControllerTS25IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // สร้าง ObjectMapper เอง ไม่ต้องพึ่ง Bean จาก Spring
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @MockitoBean
    private AdminResidentService adminResidentService;

    // TC-IT-25-01: ตรวจสอบการเพิ่มข้อมูลผู้พักอาศัยสำเร็จ
    @Test
    void TC_IT_25_01_shouldCreateResidentSuccessfully() throws Exception {
        UUID residentId = UUID.randomUUID();

        AdminResidentCreateRequest request = createRequest(
                "สมชาย", "ใจดี", "0812345678", 101);

        AdminResidentResponse response = createResponse(
                residentId, "สมชาย", "ใจดี", "0812345678", 101);

        when(adminResidentService.createResident(any(AdminResidentCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/residents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.residentId").value(residentId.toString()))
                .andExpect(jsonPath("$.firstName").value("สมชาย"))
                .andExpect(jsonPath("$.lastName").value("ใจดี"))
                .andExpect(jsonPath("$.phoneNo").value("0812345678"))
                .andExpect(jsonPath("$.roomNo").value(101));

        verify(adminResidentService)
                .createResident(any(AdminResidentCreateRequest.class));
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-25-02: ตรวจสอบว่าระบบรับข้อมูลจาก Request ได้ถูกต้อง
    @Test
    void TC_IT_25_02_shouldPassRequestFieldsToService() throws Exception {
        AdminResidentCreateRequest request = createRequest(
                "สมหญิง", "รักดี", "0898765432", 102);

        AdminResidentResponse response = createResponse(
                UUID.randomUUID(), "สมหญิง", "รักดี", "0898765432", 102);

        when(adminResidentService.createResident(any(AdminResidentCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/residents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("สมหญิง"))
                .andExpect(jsonPath("$.lastName").value("รักดี"))
                .andExpect(jsonPath("$.phoneNo").value("0898765432"))
                .andExpect(jsonPath("$.roomNo").value(102));

        verify(adminResidentService)
                .createResident(any(AdminResidentCreateRequest.class));
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-25-03: ตรวจสอบกรณีไม่พบห้องพัก
    @Test
    void TC_IT_25_03_shouldReturnInternalServerErrorWhenRoomDoesNotExist()
            throws Exception {

        AdminResidentCreateRequest request = createRequest(
                "สมชาย", "ใจดี", "0812345678", 999);

        when(adminResidentService.createResident(any(AdminResidentCreateRequest.class)))
                .thenThrow(new RuntimeException("ไม่พบห้องหมายเลข 999"));

        mockMvc.perform(post("/api/admin/residents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));

        verify(adminResidentService)
                .createResident(any(AdminResidentCreateRequest.class));
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-25-04: ตรวจสอบกรณี Service เกิดข้อผิดพลาด
    @Test
    void TC_IT_25_04_shouldReturnInternalServerErrorWhenServiceFails()
            throws Exception {

        AdminResidentCreateRequest request = createRequest(
                "สมชาย", "ใจดี", "0812345678", 101);

        when(adminResidentService.createResident(any(AdminResidentCreateRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/admin/residents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));

        verify(adminResidentService)
                .createResident(any(AdminResidentCreateRequest.class));
        verifyNoMoreInteractions(adminResidentService);
    }

    // สร้างข้อมูล Request สำหรับใช้ในแต่ละ Test Case
    private AdminResidentCreateRequest createRequest(
            String firstName,
            String lastName,
            String phoneNo,
            Integer roomNo) {

        AdminResidentCreateRequest request = new AdminResidentCreateRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhoneNo(phoneNo);
        request.setRoomNo(roomNo);

        return request;
    }

    // สร้าง Response จำลองจาก Service
    private AdminResidentResponse createResponse(
            UUID residentId,
            String firstName,
            String lastName,
            String phoneNo,
            Integer roomNo) {

        AdminResidentResponse response = new AdminResidentResponse();
        response.setResidentId(residentId);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setPhoneNo(phoneNo);
        response.setRoomNo(roomNo);

        return response;
    }
}
