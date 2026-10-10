package com.example.dormitory.controller.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.dormitory.domain.entity.Building;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.service.AdminResidentService;

@WebMvcTest(AdminResidentWebController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminResidentWebControllerTS27IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminResidentService adminResidentService;

    /**
     * สร้างข้อมูลห้องพักที่มี Building ครบถ้วน
     * เพื่อให้ Thymeleaf เข้าถึง room.building.buildingName ได้
     */
    private Room createRoom(int roomNo, int buildingNo,
                            String buildingName, int totalFloor) {

        Building building = new Building(
                buildingNo,
                buildingName,
                totalFloor
        );

        return new Room(roomNo, building);
    }

    /**
     * TC-IT-27-01
     * ตรวจสอบการเปิดหน้าจัดการผู้พักอาศัย
     * และตรวจสอบว่ามีข้อมูลห้องพักอยู่ใน Model
     */
    @Test
    void TC_IT_27_01_shouldDisplayResidentManagementPageWithRooms()
            throws Exception {

        List<Room> rooms = Arrays.asList(
                createRoom(101, 1, "อาคาร A", 5),
                createRoom(102, 1, "อาคาร A", 5),
                createRoom(201, 2, "อาคาร B", 4)
        );

        when(adminResidentService.getAllResidents())
                .thenReturn(Collections.emptyList());

        when(adminResidentService.getAllRooms())
                .thenReturn(rooms);

        mockMvc.perform(get("/admin/residents"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/residentManage"))
                .andExpect(model().attributeExists("residents"))
                .andExpect(model().attributeExists("rooms"));

        verify(adminResidentService, times(1)).getAllResidents();
        verify(adminResidentService, times(1)).getAllRooms();
    }

    /**
     * TC-IT-27-02
     * ตรวจสอบว่ารายการห้องพักจาก Service
     * ถูกส่งไปยัง Model อย่างครบถ้วน
     */
    @Test
    void TC_IT_27_02_shouldPassAllRoomsToModel()
            throws Exception {

        Room room1 = createRoom(101, 1, "อาคาร A", 5);
        Room room2 = createRoom(102, 1, "อาคาร A", 5);

        List<Room> rooms = Arrays.asList(room1, room2);

        when(adminResidentService.getAllResidents())
                .thenReturn(Collections.emptyList());

        when(adminResidentService.getAllRooms())
                .thenReturn(rooms);

        mockMvc.perform(get("/admin/residents"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/residentManage"))
                .andExpect(model().attribute("rooms", rooms))
                .andExpect(result -> {
                    Object actual = result.getModelAndView()
                            .getModel()
                            .get("rooms");

                    org.junit.jupiter.api.Assertions.assertInstanceOf(
                            List.class, actual
                    );

                    List<?> actualRooms = (List<?>) actual;

                    org.junit.jupiter.api.Assertions.assertEquals(
                            2, actualRooms.size()
                    );

                    org.junit.jupiter.api.Assertions.assertSame(
                            room1, actualRooms.get(0)
                    );

                    org.junit.jupiter.api.Assertions.assertSame(
                            room2, actualRooms.get(1)
                    );
                });

        verify(adminResidentService, times(1)).getAllRooms();
    }

    /**
     * TC-IT-27-03
     * ตรวจสอบกรณีไม่มีข้อมูลห้องพัก
     */
    @Test
    void TC_IT_27_03_shouldDisplayPageWhenNoRoomsExist()
            throws Exception {

        when(adminResidentService.getAllResidents())
                .thenReturn(Collections.emptyList());

        when(adminResidentService.getAllRooms())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/residents"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/residentManage"))
                .andExpect(model().attribute("rooms",
                        Collections.emptyList()));

        verify(adminResidentService, times(1)).getAllResidents();
        verify(adminResidentService, times(1)).getAllRooms();
    }

    /**
     * TC-IT-27-04
     * ตรวจสอบว่า Controller เรียก Service
     * เพื่อดึงทั้งข้อมูลผู้พักอาศัยและข้อมูลห้องพัก
     */
    @Test
    void TC_IT_27_04_shouldCallServiceToLoadResidentsAndRooms()
            throws Exception {

        List<AdminResidentResponse> residents =
                Collections.emptyList();

        List<Room> rooms = Collections.singletonList(
                createRoom(301, 3, "อาคาร C", 6)
        );

        when(adminResidentService.getAllResidents())
                .thenReturn(residents);

        when(adminResidentService.getAllRooms())
                .thenReturn(rooms);

        mockMvc.perform(get("/admin/residents"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/residentManage"))
                .andExpect(model().attribute("residents", residents))
                .andExpect(model().attribute("rooms", rooms));

        verify(adminResidentService, times(1)).getAllResidents();
        verify(adminResidentService, times(1)).getAllRooms();
        verifyNoMoreInteractions(adminResidentService);
    }
}
