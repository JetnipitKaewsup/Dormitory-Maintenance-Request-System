package com.example.dormitory.controller.web;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@WebMvcTest(AdminRepairRequestController.class)
@AutoConfigureMockMvc
class AdminRepairRequestControllerTS09IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    /*
     * ============================================================
     * TC-IT-09-01
     * ตรวจสอบการเปิดหน้ารายการแจ้งซ่อมทั้งหมด
     * ============================================================
     */
    @Test
    void TC_IT_09_01_shouldOpenAllRepairRequestsPage() throws Exception {

        RepairRequest request = createRepairRequest(
                RepairRequestStatus.PENDING
        );

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(request));

        mockMvc.perform(
                get("/admin/requests")
                        .with(user("integration_admin")
                                .roles("ADMIN"))
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/requests-list"));
    }

    /*
     * ============================================================
     * TC-IT-09-02
     * ตรวจสอบการโหลดรายการแจ้งซ่อมทั้งหมด
     * ============================================================
     */
    @Test
    void TC_IT_09_02_shouldLoadAllRepairRequests() throws Exception {

        RepairRequest request1 = createRepairRequest(
                RepairRequestStatus.PENDING
        );

        RepairRequest request2 = createRepairRequest(
                RepairRequestStatus.COMPLETED
        );

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(request1, request2));

        mockMvc.perform(
                get("/admin/requests")
                        .with(user("integration_admin")
                                .roles("ADMIN"))
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/requests-list"));
    }

    /*
     * ============================================================
     * TC-IT-09-03
     * ตรวจสอบการแสดงรายการแจ้งซ่อมหลายรายการ
     * ============================================================
     */
    @Test
    void TC_IT_09_03_shouldDisplayMultipleRepairRequests()
            throws Exception {

        RepairRequest pending = createRepairRequest(
                RepairRequestStatus.PENDING
        );

        RepairRequest approved = createRepairRequest(
                RepairRequestStatus.APPROVED
        );

        RepairRequest completed = createRepairRequest(
                RepairRequestStatus.COMPLETED
        );

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(
                        pending,
                        approved,
                        completed
                ));

        mockMvc.perform(
                get("/admin/requests")
                        .with(user("integration_admin")
                                .roles("ADMIN"))
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/requests-list"));
    }

    /*
     * ============================================================
     * TC-IT-09-04
     * ตรวจสอบกรณีไม่มีรายการแจ้งซ่อม
     * ============================================================
     */
    @Test
    void TC_IT_09_04_shouldHandleEmptyRepairRequestList()
            throws Exception {

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/admin/requests")
                        .with(user("integration_admin")
                                .roles("ADMIN"))
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/requests-list"));
    }

    /*
     * ============================================================
     * TC-IT-09-05
     * ตรวจสอบ Admin สามารถเข้าถึงหน้ารายการแจ้งซ่อม
     * ============================================================
     */
    @Test
    void TC_IT_09_05_shouldAllowAdminToAccessRepairRequests()
            throws Exception {

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of());

        mockMvc.perform(
                get("/admin/requests")
                        .with(user("integration_admin")
                                .roles("ADMIN"))
        )
        .andExpect(status().isOk())
        .andExpect(view().name("admin/requests-list"));
    }

    /*
     * ============================================================
     * Helper สำหรับสร้าง RepairRequest Mock
     *
     * Template admin/requests-list.html ต้องการข้อมูล:
     *
     * request.reporter.user.firstName
     * request.reporter.user.lastName
     * request.room.building.buildingName
     * request.room.roomNo
     * request.repairType.thaiName
     * request.status.cssClass
     * request.status.thaiName
     * request.startDateTime
     *
     * ดังนั้นต้องสร้าง nested object ให้ครบ
     * ============================================================
     */
    private RepairRequest createRepairRequest(
            RepairRequestStatus status) {

        RepairRequest request = org.mockito.Mockito
                .mock(RepairRequest.class);

        Reporter reporter = org.mockito.Mockito
                .mock(Reporter.class);

        User user = org.mockito.Mockito
                .mock(User.class);

        Room room = org.mockito.Mockito
                .mock(Room.class);

        Building building = org.mockito.Mockito
                .mock(Building.class);

        RepairType repairType = RepairType.ELECTRICAL;

        when(request.getRepairRequestId())
                .thenReturn(UUID.randomUUID());

        when(request.getReporter())
                .thenReturn(reporter);

        when(reporter.getUser())
                .thenReturn(user);

        when(user.getFirstName())
                .thenReturn("Integration");

        when(user.getLastName())
                .thenReturn("Tester");

        when(request.getRoom())
                .thenReturn(room);

        when(room.getBuilding())
                .thenReturn(building);

        when(building.getBuildingName())
                .thenReturn("Building A");

        when(room.getRoomNo())
                .thenReturn(101);

        when(request.getRepairType())
                .thenReturn(repairType);

        when(request.getStatus())
                .thenReturn(status);

        when(request.getStartDateTime())
                .thenReturn(LocalDateTime.of(
                        2026,
                        10,
                        8,
                        10,
                        0
                ));

        return request;
    }
}