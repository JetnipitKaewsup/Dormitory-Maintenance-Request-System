package com.example.dormitory.controller.web;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.service.AdminTechnicianService;

class AdminTechnicianWebControllerTS17UnitTest {

    private AdminTechnicianService adminTechnicianService;
    private AdminTechnicianWebController controller;
    private Model model;

    @BeforeEach
    void setUp() {
        // สร้าง Mock Service เพื่อไม่ต้องเชื่อมต่อฐานข้อมูลจริง
        adminTechnicianService = mock(AdminTechnicianService.class);

        // สร้าง Controller โดยส่ง Mock Service เข้าไป
        controller = new AdminTechnicianWebController(adminTechnicianService);

        // เตรียม Model สำหรับตรวจสอบข้อมูลที่ Controller ส่งไปยัง View
        model = new ExtendedModelMap();
    }

    @Test
    void TC_UT_17_01_shouldDisplayAllTechniciansWhenDataExists() {
        // Arrange: เตรียมข้อมูลช่างตัวอย่าง 2 รายการ
        AdminTechnicianResponse technician1 = new AdminTechnicianResponse(
                UUID.randomUUID(),
                "ระบบไฟฟ้า",
                UUID.randomUUID(),
                "ทดสอบ",
                "ช่างหนึ่ง",
                "0812345678",
                "technician01"
        );

        AdminTechnicianResponse technician2 = new AdminTechnicianResponse(
                UUID.randomUUID(),
                "ระบบประปา",
                UUID.randomUUID(),
                "ทดสอบ",
                "ช่างสอง",
                "0823456789",
                "technician02"
        );

        List<AdminTechnicianResponse> technicians =
                List.of(technician1, technician2);

        when(adminTechnicianService.getAllTechnicians())
                .thenReturn(technicians);

        // Act: เรียก Controller เพื่อแสดงรายการช่าง
        String viewName = controller.listTechniciansPage(model);

        // Assert: ตรวจสอบ View และข้อมูลใน Model
        assertEquals("admin/technicianManage", viewName);
        assertSame(technicians, model.getAttribute("technicians"));
        assertEquals(2,
                ((List<?>) model.getAttribute("technicians")).size());

        // ตรวจสอบว่า Controller เรียก Service จริง 1 ครั้ง
        verify(adminTechnicianService).getAllTechnicians();
    }

    @Test
    void TC_UT_17_02_shouldDisplayEmptyListWhenNoTechniciansExist() {
        // Arrange: จำลองกรณีไม่มีข้อมูลช่างในระบบ
        List<AdminTechnicianResponse> technicians = List.of();

        when(adminTechnicianService.getAllTechnicians())
                .thenReturn(technicians);

        // Act: เรียก Controller เพื่อแสดงรายการช่าง
        String viewName = controller.listTechniciansPage(model);

        // Assert: ต้องแสดง View เดิมและส่งรายการว่างไปยัง Model
        assertEquals("admin/technicianManage", viewName);
        assertSame(technicians, model.getAttribute("technicians"));
        assertEquals(0,
                ((List<?>) model.getAttribute("technicians")).size());

        verify(adminTechnicianService).getAllTechnicians();
    }

    @Test
    void TC_UT_17_03_shouldCallServiceOnceToRetrieveTechnicians() {
        // Arrange: จำลองผลลัพธ์จาก Service
        when(adminTechnicianService.getAllTechnicians())
                .thenReturn(List.of());

        // Act: เรียก Controller
        controller.listTechniciansPage(model);

        // Assert: ตรวจสอบว่ามีการเรียก Service 1 ครั้งเท่านั้น
        verify(adminTechnicianService,
                org.mockito.Mockito.times(1))
                .getAllTechnicians();
    }
}
