package com.example.dormitory.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.repository.AdminResidentRepository;
import com.example.dormitory.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class AdminResidentServiceTS27UnitTest {

    @Mock
    private AdminResidentRepository residentRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminResidentServiceImpl adminResidentService;

    /**
     * TC-UT-27-01
     * ตรวจสอบว่าสามารถดึงข้อมูลห้องพักทั้งหมดได้
     */
    @Test
    void TC_UT_27_01_shouldReturnAllRooms() {
        Room room1 = mock(Room.class);
        Room room2 = mock(Room.class);
        Room room3 = mock(Room.class);

        List<Room> expectedRooms = Arrays.asList(
                room1,
                room2,
                room3
        );

        when(roomRepository.findAll(any(Sort.class)))
                .thenReturn(expectedRooms);

        List<Room> actualRooms = adminResidentService.getAllRooms();

        assertNotNull(actualRooms);
        assertEquals(3, actualRooms.size());
        assertSame(room1, actualRooms.get(0));
        assertSame(room2, actualRooms.get(1));
        assertSame(room3, actualRooms.get(2));

        verify(roomRepository, times(1))
                .findAll(any(Sort.class));

        verifyNoInteractions(residentRepository);
    }

    /**
     * TC-UT-27-02
     * ตรวจสอบว่า Service ส่ง Sort สำหรับเรียง roomNo
     * จากน้อยไปมากให้ Repository
     */
    @Test
    void TC_UT_27_02_shouldRequestRoomsSortedByRoomNoAscending() {
        List<Room> expectedRooms = Arrays.asList(
                mock(Room.class),
                mock(Room.class)
        );

        when(roomRepository.findAll(any(Sort.class)))
                .thenReturn(expectedRooms);

        adminResidentService.getAllRooms();

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(Sort.class);

        verify(roomRepository).findAll(sortCaptor.capture());

        Sort actualSort = sortCaptor.getValue();
        Sort.Order roomNoOrder = actualSort.getOrderFor("roomNo");

        assertNotNull(roomNoOrder);
        assertEquals(Sort.Direction.ASC, roomNoOrder.getDirection());
    }

    /**
     * TC-UT-27-03
     * ตรวจสอบกรณีไม่มีข้อมูลห้องพักในระบบ
     */
    @Test
    void TC_UT_27_03_shouldReturnEmptyListWhenNoRoomsExist() {
        when(roomRepository.findAll(any(Sort.class)))
                .thenReturn(Collections.emptyList());

        List<Room> actualRooms = adminResidentService.getAllRooms();

        assertNotNull(actualRooms);
        assertTrue(actualRooms.isEmpty());

        verify(roomRepository, times(1))
                .findAll(any(Sort.class));

        verifyNoInteractions(residentRepository);
    }

    /**
     * TC-UT-27-04
     * ตรวจสอบว่า Service ส่งต่อข้อผิดพลาดจาก Repository
     */
    @Test
    void TC_UT_27_04_shouldPropagateExceptionWhenRepositoryFails() {
        when(roomRepository.findAll(any(Sort.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminResidentService.getAllRooms()
        );

        assertEquals("Database error", exception.getMessage());

        verify(roomRepository, times(1))
                .findAll(any(Sort.class));

        verifyNoInteractions(residentRepository);
    }
}
