package com.example.dormitory.controller.web;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import com.example.dormitory.domain.entity.Building;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.enums.RepairType;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

/**
 * TS-10
 * ดูรายละเอียดคำร้อง
 *
 * Integration Test สำหรับ AdminRepairRequestController
 */
@WebMvcTest(AdminRepairRequestController.class)
@AutoConfigureMockMvc
class AdminRepairRequestControllerTS10IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    private UUID requestId;
    private RepairRequest request;

    @BeforeEach
    void setUp() {

        requestId = UUID.randomUUID();

        request = org.mockito.Mockito.mock(RepairRequest.class);

        Reporter reporter = org.mockito.Mockito.mock(Reporter.class);
        User user = org.mockito.Mockito.mock(User.class);
        Room room = org.mockito.Mockito.mock(Room.class);
        Building building = org.mockito.Mockito.mock(Building.class);

        // Mock ข้อมูลคำร้อง
        when(request.getRepairRequestId()).thenReturn(requestId);

        when(request.getStatus())
                .thenReturn(RepairRequestStatus.PENDING);

        when(request.getStartDateTime())
                .thenReturn(LocalDateTime.of(2026, 10, 8, 10, 0));

        when(request.getEndDateTime())
                .thenReturn(LocalDateTime.of(2026, 10, 8, 11, 0));

        when(request.getRepairType())
                .thenReturn(RepairType.ELECTRICAL);

        when(request.getDescription())
                .thenReturn("ระบบไฟฟ้าภายในห้องมีปัญหา");

        when(request.getReporterNote())
                .thenReturn("กรุณาตรวจสอบโดยเร็ว");

        // Mock Reporter
        when(request.getReporter()).thenReturn(reporter);
        when(reporter.getUser()).thenReturn(user);

        when(user.getFirstName()).thenReturn("Integration");
        when(user.getLastName()).thenReturn("Tester");
        when(user.getPhoneNo()).thenReturn("0812345678");

        // Mock Room / Building
        when(request.getRoom()).thenReturn(room);
        when(room.getBuilding()).thenReturn(building);
        when(building.getBuildingName()).thenReturn("Building A");
        when(room.getRoomNo()).thenReturn(101);

        // Mock Service
        when(repairRequestService.getById(requestId))
                .thenReturn(request);
    }

    /**
     * กำหนด CSRF token ใน request attribute
     * เพื่อให้ Thymeleaf อ่าน ${_csrf.token} ได้
     * ระหว่างการทดสอบหน้าเว็บด้วย MockMvc
     */
    private RequestPostProcessor withCsrfRequestAttribute() {
        return servletRequest -> {
            CsrfToken csrfToken = new DefaultCsrfToken(
                    "X-CSRF-TOKEN",
                    "_csrf",
                    "test-csrf-token"
            );

            servletRequest.setAttribute(
                    CsrfToken.class.getName(),
                    csrfToken
            );

            servletRequest.setAttribute("_csrf", csrfToken);

            return servletRequest;
        };
    }

    /**
     * TC-IT-10-01
     * ตรวจสอบการเปิดหน้ารายละเอียดคำร้อง
     */
    @Test
    void TC_IT_10_01_shouldOpenRepairRequestDetailPage()
            throws Exception {

        mockMvc.perform(
                get("/admin/requests/{id}", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/repair-request-detail"));
    }

    /**
     * TC-IT-10-02
     * ตรวจสอบว่าข้อมูลคำร้องถูกส่งเข้า Model
     */
    @Test
    void TC_IT_10_02_shouldAddRequestToModel()
            throws Exception {

        mockMvc.perform(
                get("/admin/requests/{id}", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(model().attribute("request", request));
    }

    /**
     * TC-IT-10-03
     * ตรวจสอบการเรียก Service ด้วย Request ID
     */
    @Test
    void TC_IT_10_03_shouldGetRequestById()
            throws Exception {

        mockMvc.perform(
                get("/admin/requests/{id}", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk());

        verify(repairRequestService).getById(requestId);
    }

    /**
     * TC-IT-10-04
     * ตรวจสอบการแสดงรายละเอียดของคำร้อง
     * ที่มีสถานะ PENDING
     */
    @Test
    void TC_IT_10_04_shouldDisplayPendingRequestDetail()
            throws Exception {

        when(request.getStatus())
                .thenReturn(RepairRequestStatus.PENDING);

        mockMvc.perform(
                get("/admin/requests/{id}", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(model().attribute("request", request))
        .andExpect(view().name("admin/repair-request-detail"));
    }

    /**
     * TC-IT-10-05
     * ตรวจสอบ URL สำหรับดูรายละเอียดคำร้อง
     */
    @Test
    void TC_IT_10_05_shouldAccessCorrectDetailUrl()
            throws Exception {

        mockMvc.perform(
                get("/admin/requests/{id}", requestId)
                        .with(withCsrfRequestAttribute())
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/repair-request-detail"))
        .andExpect(model().attributeExists("request"));
    }
}
