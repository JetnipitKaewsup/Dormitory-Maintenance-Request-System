
package com.example.dormitory.service.impl;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import com.example.dormitory.dto.request.AdminResidentUpdateRequest;
import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.repository.AdminResidentRepository;
import com.example.dormitory.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class AdminResidentServiceTS26UnitTest {

    @Mock
    private AdminResidentRepository residentRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminResidentServiceImpl adminResidentService;

    private UUID residentId;
    private Room oldRoom;
    private Room newRoom;
    private Resident resident;

    @BeforeEach
    void setUp() {
        residentId = UUID.randomUUID();

        oldRoom = new Room();
        oldRoom.setRoomNo(101);

        newRoom = new Room();
        newRoom.setRoomNo(102);

        resident = new Resident(
                oldRoom,
                "สมชาย",
                "ใจดี",
                "0812345678"
        );
    }

    // TC-UT-26-01:
    // ตรวจสอบการแก้ไขข้อมูลผู้พักอาศัยสำเร็จทุกฟิลด์
    @Test
    void TC_UT_26_01_shouldUpdateResidentSuccessfully() {
        AdminResidentUpdateRequest request = new AdminResidentUpdateRequest();
        request.setFirstName("สมศักดิ์");
        request.setLastName("รักดี");
        request.setPhoneNo("0898765432");
        request.setRoomNo(102);

        when(residentRepository.findById(residentId))
                .thenReturn(Optional.of(resident));
        when(roomRepository.findById(102))
                .thenReturn(Optional.of(newRoom));
        when(residentRepository.save(any(Resident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminResidentResponse response =
                adminResidentService.updateResident(residentId, request);

        assertNotNull(response);
        assertEquals("สมศักดิ์", response.getFirstName());
        assertEquals("รักดี", response.getLastName());
        assertEquals("0898765432", response.getPhoneNo());
        assertEquals(102, response.getRoomNo());

        verify(residentRepository).findById(residentId);
        verify(roomRepository).findById(102);
        verify(residentRepository).save(resident);
        verifyNoMoreInteractions(residentRepository, roomRepository);
    }

    // TC-UT-26-02:
    // ตรวจสอบการแก้ไขเฉพาะบางฟิลด์ โดยฟิลด์ที่เป็น null ต้องคงค่าเดิม
    @Test
    void TC_UT_26_02_shouldUpdateOnlyProvidedFields() {
        AdminResidentUpdateRequest request = new AdminResidentUpdateRequest();
        request.setFirstName("สมศักดิ์");
        // ไม่ส่ง lastName, phoneNo และ roomNo

        when(residentRepository.findById(residentId))
                .thenReturn(Optional.of(resident));
        when(residentRepository.save(any(Resident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdminResidentResponse response =
                adminResidentService.updateResident(residentId, request);

        assertEquals("สมศักดิ์", response.getFirstName());
        assertEquals("ใจดี", response.getLastName());
        assertEquals("0812345678", response.getPhoneNo());
        assertEquals(101, response.getRoomNo());

        verify(residentRepository).findById(residentId);
        verify(residentRepository).save(resident);
        verify(roomRepository, never()).findById(any());
        verifyNoMoreInteractions(residentRepository, roomRepository);
    }

    // TC-UT-26-03:
    // ตรวจสอบกรณีไม่พบผู้พักอาศัยตาม ID
    @Test
    void TC_UT_26_03_shouldThrowExceptionWhenResidentDoesNotExist() {
        AdminResidentUpdateRequest request = new AdminResidentUpdateRequest();
        request.setFirstName("สมศักดิ์");

        when(residentRepository.findById(residentId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminResidentService.updateResident(residentId, request)
        );

        assertEquals("ไม่พบข้อมูลผู้พักอาศัย", exception.getMessage());

        verify(residentRepository).findById(residentId);
        verify(residentRepository, never()).save(any(Resident.class));
        verifyNoMoreInteractions(residentRepository, roomRepository);
    }

    // TC-UT-26-04:
    // ตรวจสอบกรณีไม่พบห้องพักใหม่ที่ต้องการย้ายไป
    @Test
    void TC_UT_26_04_shouldThrowExceptionWhenNewRoomDoesNotExist() {
        AdminResidentUpdateRequest request = new AdminResidentUpdateRequest();
        request.setRoomNo(999);

        when(residentRepository.findById(residentId))
                .thenReturn(Optional.of(resident));
        when(roomRepository.findById(999))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminResidentService.updateResident(residentId, request)
        );

        assertEquals("ไม่พบห้องหมายเลข 999", exception.getMessage());

        verify(residentRepository).findById(residentId);
        verify(roomRepository).findById(999);
        verify(residentRepository, never()).save(any(Resident.class));
        verifyNoMoreInteractions(residentRepository, roomRepository);
    }
}
