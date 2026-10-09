
package com.example.dormitory.controller;

import com.example.dormitory.controller.web.RepairWorkController;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.TechnicianProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairWorkControllerTS33UnitTest {

    @Mock
    private RepairAssignmentService repairAssignmentService;

    @Mock
    private TechnicianProfileService technicianProfileService;

    @Mock
    private User user;

    @Mock
    private Technician technician;

    @InjectMocks
    private RepairWorkController repairWorkController;

    private UUID userId;
    private UUID technicianId;
    private Model model;

    @BeforeEach
    void setUp() {
        userId = UUID.fromString(
                "44444444-4444-4444-4444-444444444444");

        technicianId = UUID.fromString(
                "55555555-5555-5555-5555-555555555555");

        model = new ExtendedModelMap();
    }

    @Test
    @DisplayName("TC-UT-33-01: แสดงข้อมูลส่วนตัวของช่างสำเร็จ")
    void shouldDisplayTechnicianProfileSuccessfully() {
        when(user.getUserId()).thenReturn(userId);
        when(technicianProfileService.getTechnicianByUserId(userId))
                .thenReturn(technician);

        String viewName = repairWorkController.profile(user, model);

        assertEquals("technician/profile", viewName);
        assertSame(technician, model.getAttribute("technician"));

        verify(technicianProfileService)
                .getTechnicianByUserId(userId);
    }

    @Test
    @DisplayName("TC-UT-33-02: ค้นหาข้อมูลช่างด้วย User ID ของผู้เข้าสู่ระบบ")
    void shouldFindTechnicianUsingAuthenticatedUserId() {
        when(user.getUserId()).thenReturn(userId);
        when(technicianProfileService.getTechnicianByUserId(userId))
                .thenReturn(technician);

        repairWorkController.profile(user, model);

        verify(technicianProfileService, times(1))
                .getTechnicianByUserId(userId);

        verifyNoMoreInteractions(technicianProfileService);
    }

    @Test
    @DisplayName("TC-UT-33-03: เปลี่ยนเส้นทางเมื่อ User ID เป็น null")
    void shouldRedirectToLoginWhenUserIdIsNull() {
        when(user.getUserId()).thenReturn(null);

        String viewName = repairWorkController.profile(user, model);

        assertEquals("redirect:/login", viewName);
        verifyNoInteractions(technicianProfileService);
    }

    @Test
    @DisplayName("TC-UT-33-04: ส่งข้อมูลช่างไปยัง Model")
    void shouldAddTechnicianToModel() {
        when(user.getUserId()).thenReturn(userId);
        when(technicianProfileService.getTechnicianByUserId(userId))
                .thenReturn(technician);

        String viewName = repairWorkController.profile(user, model);

        assertEquals("technician/profile", viewName);
        assertNotNull(model.getAttribute("technician"));
        assertSame(technician, model.getAttribute("technician"));
    }

    @Test
    @DisplayName("TC-UT-33-05: ไม่เรียก Service อื่นเมื่อแสดงโปรไฟล์ช่าง")
    void shouldOnlyCallTechnicianProfileService() {
        when(user.getUserId()).thenReturn(userId);
        when(technicianProfileService.getTechnicianByUserId(userId))
                .thenReturn(technician);

        repairWorkController.profile(user, model);

        verify(technicianProfileService)
                .getTechnicianByUserId(userId);

        verifyNoInteractions(repairAssignmentService);
    }
}
