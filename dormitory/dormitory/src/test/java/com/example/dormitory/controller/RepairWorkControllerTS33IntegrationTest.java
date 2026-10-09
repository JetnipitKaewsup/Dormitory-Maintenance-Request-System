
package com.example.dormitory.controller;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.dormitory.controller.web.RepairWorkController;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.repository.TechnicianRepository;
import com.example.dormitory.service.RepairAssignmentService;

@SpringBootTest
class RepairWorkControllerTS33IntegrationTest {

    @Autowired
    private RepairWorkController repairWorkController;

    // Mock Repository เพื่อไม่เชื่อมต่อฐานข้อมูลจริง
    @MockitoBean
    private TechnicianRepository technicianRepository;

    // Mock service ที่ไม่เกี่ยวข้องกับการดูโปรไฟล์
    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    private User user;
    private Technician technician;

    private UUID userId;
    private UUID technicianId;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        technician = mock(Technician.class);

        userId = UUID.randomUUID();
        technicianId = UUID.randomUUID();

        when(user.getUserId()).thenReturn(userId);
        when(technician.getTechnicianId()).thenReturn(technicianId);
    }

    // TC-IT-33-01
    // ตรวจสอบการแสดงหน้าโปรไฟล์ของช่างสำเร็จ
    @Test
    void TC_IT_33_01_userCanViewTechnicianProfile() {
        when(technicianRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(technician));

        Model model = new ExtendedModelMap();

        String view = repairWorkController.profile(user, model);

        assertEquals("technician/profile", view);
        assertSame(technician, model.getAttribute("technician"));

        verify(technicianRepository).findByUserUserId(userId);
    }

    // TC-IT-33-02
    // ตรวจสอบการค้นหาข้อมูลช่างด้วย User ID
    @Test
    void TC_IT_33_02_systemFindsTechnicianByUserId() {
        when(technicianRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(technician));

        Model model = new ExtendedModelMap();

        repairWorkController.profile(user, model);

        verify(technicianRepository, times(1))
                .findByUserUserId(userId);
    }

    // TC-IT-33-03
    // ตรวจสอบกรณี User ID เป็น null
    @Test
    void TC_IT_33_03_nullUserIdRedirectsToLogin() {
        when(user.getUserId()).thenReturn(null);

        Model model = new ExtendedModelMap();

        String view = repairWorkController.profile(user, model);

        assertEquals("redirect:/login", view);
        assertNull(model.getAttribute("technician"));

        verifyNoInteractions(technicianRepository);
        verifyNoInteractions(repairAssignmentService);
    }

    // TC-IT-33-04
    // ตรวจสอบการเพิ่มข้อมูลช่างลงใน Model
    @Test
    void TC_IT_33_04_technicianIsAddedToModel() {
        when(technicianRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(technician));

        Model model = new ExtendedModelMap();

        repairWorkController.profile(user, model);

        assertTrue(model.containsAttribute("technician"));
        assertSame(technician, model.getAttribute("technician"));
    }

    // TC-IT-33-05
    // ตรวจสอบกรณีไม่พบข้อมูลช่าง
    @Test
    void TC_IT_33_05_throwsExceptionWhenTechnicianNotFound() {
        when(technicianRepository.findByUserUserId(userId))
                .thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repairWorkController.profile(user, model)
        );

        assertEquals("ไม่พบข้อมูลช่าง", exception.getMessage());

        verify(technicianRepository).findByUserUserId(userId);
        verifyNoInteractions(repairAssignmentService);
    }
}
