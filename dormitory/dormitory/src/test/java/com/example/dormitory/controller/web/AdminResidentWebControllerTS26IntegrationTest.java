
package com.example.dormitory.controller.web;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dormitory.dto.request.AdminResidentUpdateRequest;
import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.service.AdminResidentService;

@WebMvcTest(AdminResidentWebController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminResidentWebControllerTS26IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminResidentService adminResidentService;

    // TC-IT-26-01:
    // ตรวจสอบการแก้ไขข้อมูลผู้พักอาศัยสำเร็จ
    @Test
    void TC_IT_26_01_shouldUpdateResidentSuccessfully() throws Exception {
        UUID residentId = UUID.randomUUID();

        AdminResidentResponse response = new AdminResidentResponse();
        response.setResidentId(residentId);
        response.setFirstName("สมศักดิ์");
        response.setLastName("รักดี");
        response.setPhoneNo("0898765432");
        response.setRoomNo(102);

        when(adminResidentService.updateResident(
                eq(residentId), any(AdminResidentUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/admin/residents/update")
                        .param("residentId", residentId.toString())
                        .param("firstName", "สมศักดิ์")
                        .param("lastName", "รักดี")
                        .param("phoneNo", "0898765432")
                        .param("roomNo", "102"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/residents"))
                .andExpect(flash().attribute(
                        "success", "บันทึกข้อมูลผู้พักอาศัยสำเร็จ"));

        verify(adminResidentService).updateResident(
                eq(residentId), any(AdminResidentUpdateRequest.class));
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-26-02:
    // ตรวจสอบการแก้ไขเฉพาะบางฟิลด์
    @Test
    void TC_IT_26_02_shouldUpdateOnlyProvidedFields() throws Exception {
        UUID residentId = UUID.randomUUID();

        AdminResidentResponse response = new AdminResidentResponse();
        response.setResidentId(residentId);
        response.setFirstName("สมศักดิ์");
        response.setLastName("ใจดี");
        response.setPhoneNo("0812345678");
        response.setRoomNo(101);

        when(adminResidentService.updateResident(
                eq(residentId), any(AdminResidentUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/admin/residents/update")
                        .param("residentId", residentId.toString())
                        .param("firstName", "สมศักดิ์"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/residents"))
                .andExpect(flash().attribute(
                        "success", "บันทึกข้อมูลผู้พักอาศัยสำเร็จ"));

        verify(adminResidentService).updateResident(
                eq(residentId), any(AdminResidentUpdateRequest.class));
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-26-03:
    // ตรวจสอบกรณี Service แจ้งว่าไม่พบผู้พักอาศัย
    @Test
    void TC_IT_26_03_shouldShowErrorWhenResidentDoesNotExist()
            throws Exception {
        UUID residentId = UUID.randomUUID();

        when(adminResidentService.updateResident(
                eq(residentId), any(AdminResidentUpdateRequest.class)))
                .thenThrow(new RuntimeException("ไม่พบข้อมูลผู้พักอาศัย"));

        mockMvc.perform(post("/admin/residents/update")
                        .param("residentId", residentId.toString())
                        .param("firstName", "สมศักดิ์"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/residents"))
                .andExpect(flash().attribute(
                        "error", "ไม่พบข้อมูลผู้พักอาศัย"));

        verify(adminResidentService).updateResident(
                eq(residentId), any(AdminResidentUpdateRequest.class));
        verifyNoMoreInteractions(adminResidentService);
    }

    // TC-IT-26-04:
    // ตรวจสอบกรณีข้อมูลไม่ถูกต้องตาม Validation
    @Test
    void TC_IT_26_04_shouldRedirectWhenPhoneNumberIsInvalid()
            throws Exception {
        UUID residentId = UUID.randomUUID();

        mockMvc.perform(post("/admin/residents/update")
                        .param("residentId", residentId.toString())
                        .param("firstName", "สมศักดิ์")
                        .param("phoneNo", "ABC123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/residents"))
                .andExpect(flash().attribute(
                        "error", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง"));

        verifyNoMoreInteractions(adminResidentService);
    }
}
