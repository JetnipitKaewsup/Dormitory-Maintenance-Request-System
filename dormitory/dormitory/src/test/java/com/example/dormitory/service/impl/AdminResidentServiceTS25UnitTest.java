
package com.example.dormitory.service.impl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.dto.request.AdminResidentCreateRequest;
import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.repository.AdminResidentRepository;
import com.example.dormitory.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class AdminResidentServiceTS25UnitTest {

    @Mock
    private AdminResidentRepository residentRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminResidentServiceImpl adminResidentService;

    private AdminResidentCreateRequest request;
    private Room room;

    @BeforeEach
    void setUp() {
        request = new AdminResidentCreateRequest();
        request.setFirstName("สมชาย");
        request.setLastName("ใจดี");
        request.setPhoneNo("0812345678");
        request.setRoomNo(101);

        room = new Room();
        room.setRoomNo(101);
    }

    // TC-UT-25-01: เพิ่มข้อมูลผู้พักอาศัยสำเร็จ
    @Test
    void TC_UT_25_01_shouldCreateResidentSuccessfully() {
        UUID residentId = UUID.randomUUID();

        when(roomRepository.findById(101))
                .thenReturn(Optional.of(room));

        when(residentRepository.save(any(Resident.class)))
                .thenAnswer(invocation -> {
                    Resident resident = invocation.getArgument(0);
                    resident.setResidentId(residentId);
                    return resident;
                });

        AdminResidentResponse response =
                adminResidentService.createResident(request);

        assertNotNull(response);
        assertEquals(residentId, response.getResidentId());
        assertEquals("สมชาย", response.getFirstName());
        assertEquals("ใจดี", response.getLastName());
        assertEquals("0812345678", response.getPhoneNo());
        assertEquals(101, response.getRoomNo());

        verify(roomRepository).findById(101);
        verify(residentRepository).save(any(Resident.class));
        verifyNoMoreInteractions(roomRepository, residentRepository);
    }

    // TC-UT-25-02: ตรวจสอบข้อมูลที่ส่งให้ Repository บันทึก
    @Test
    void TC_UT_25_02_shouldSaveResidentWithCorrectFields() {
        when(roomRepository.findById(101))
                .thenReturn(Optional.of(room));

        when(residentRepository.save(any(Resident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        adminResidentService.createResident(request);

        ArgumentCaptor<Resident> captor =
                ArgumentCaptor.forClass(Resident.class);

        verify(residentRepository).save(captor.capture());

        Resident savedResident = captor.getValue();

        assertEquals("สมชาย", savedResident.getFirstName());
        assertEquals("ใจดี", savedResident.getLastName());
        assertEquals("0812345678", savedResident.getPhoneNo());
        assertEquals(room, savedResident.getRoom());
        assertEquals(101, savedResident.getRoom().getRoomNo());

        verify(roomRepository).findById(101);
        verifyNoMoreInteractions(roomRepository, residentRepository);
    }

    // TC-UT-25-03: ไม่พบห้องที่ระบุ
    @Test
    void TC_UT_25_03_shouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById(101))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminResidentService.createResident(request)
        );

        assertEquals("ไม่พบห้องหมายเลข 101", exception.getMessage());

        verify(roomRepository).findById(101);
        verify(residentRepository, never()).save(any(Resident.class));
        verifyNoMoreInteractions(roomRepository, residentRepository);
    }

    // TC-UT-25-04: Repository เกิดข้อผิดพลาดขณะบันทึกข้อมูล
    @Test
    void TC_UT_25_04_shouldPropagateExceptionWhenSavingFails() {
        when(roomRepository.findById(101))
                .thenReturn(Optional.of(room));

        when(residentRepository.save(any(Resident.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminResidentService.createResident(request)
        );

        assertEquals("Database error", exception.getMessage());

        verify(roomRepository).findById(101);
        verify(residentRepository).save(any(Resident.class));
        verifyNoMoreInteractions(roomRepository, residentRepository);
    }
}
