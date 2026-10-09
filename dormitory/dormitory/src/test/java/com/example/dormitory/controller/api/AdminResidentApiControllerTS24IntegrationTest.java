package com.example.dormitory.controller.api;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.service.AdminResidentService;

@WebMvcTest(AdminResidentApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminResidentApiControllerTS24IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminResidentService adminResidentService;

    // TC-IT-24-01:
    // ตรวจสอบการเรียก API เพื่อดึงรายการผู้พักอาศัยทั้งหมด
    @Test
    void TC_IT_24_01_shouldReturnAllResidentsSuccessfully() throws Exception {
        UUID residentId1 = UUID.randomUUID();
        UUID residentId2 = UUID.randomUUID();

        AdminResidentResponse resident1 = createResident(
                residentId1, "สมชาย", "ใจดี", "0812345678", 101);

        AdminResidentResponse resident2 = createResident(
                residentId2, "สมหญิง", "รักดี", "0898765432", 102);

        when(adminResidentService.getAllResidents())
                .thenReturn(List.of(resident1, resident2));

        mockMvc.perform(get("/api/admin/residents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].residentId").value(residentId1.toString()))
                .andExpect(jsonPath("$[0].firstName").value("สมชาย"))
                .andExpect(jsonPath("$[0].lastName").value("ใจดี"))
                .andExpect(jsonPath("$[0].phoneNo").value("0812345678"))
                .andExpect(jsonPath("$[0].roomNo").value(101))
                .andExpect(jsonPath("$[1].residentId").value(residentId2.toString()))
                .andExpect(jsonPath("$[1].firstName").value("สมหญิง"))
                .andExpect(jsonPath("$[1].lastName").value("รักดี"))
                .andExpect(jsonPath("$[1].phoneNo").value("0898765432"))
                .andExpect(jsonPath("$[1].roomNo").value(102));

        verify(adminResidentService).getAllResidents();
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-24-02:
    // ตรวจสอบกรณีไม่มีข้อมูลผู้พักอาศัย
    @Test
    void TC_IT_24_02_shouldReturnEmptyArrayWhenNoResidentsExist() throws Exception {
        when(adminResidentService.getAllResidents())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/admin/residents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(adminResidentService).getAllResidents();
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-24-03:
    // ตรวจสอบว่า Controller เรียกใช้ Service เพื่อดึงข้อมูล
    @Test
    void TC_IT_24_03_shouldCallServiceToGetAllResidents() throws Exception {
        when(adminResidentService.getAllResidents())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/admin/residents"))
                .andExpect(status().isOk());

        verify(adminResidentService).getAllResidents();
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-24-04:
    // ตรวจสอบกรณี Service เกิด RuntimeException
    @Test
    void TC_IT_24_04_shouldReturnInternalServerErrorWhenServiceFails()
            throws Exception {

        when(adminResidentService.getAllResidents())
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/admin/residents"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"));

        verify(adminResidentService).getAllResidents();
        verifyNoMoreInteractions(adminResidentService);
    }

    private AdminResidentResponse createResident(
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
