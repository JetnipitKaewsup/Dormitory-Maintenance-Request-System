package com.example.dormitory.service.impl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.dto.response.AdminResidentResponse;
import com.example.dormitory.repository.AdminResidentRepository;
import com.example.dormitory.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class AdminResidentServiceTS24UnitTest {

    @Mock
    private AdminResidentRepository residentRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AdminResidentServiceImpl adminResidentService;

    // TC-UT-24-01:
    // ตรวจสอบว่าระบบแสดงรายการผู้พักอาศัยทั้งหมดได้
    @Test
    void shouldReturnAllResidents() {
        Resident resident1 = createResident(
                UUID.randomUUID(),
                "สมชาย",
                "ใจดี",
                "0812345678",
                101
        );

        Resident resident2 = createResident(
                UUID.randomUUID(),
                "สมหญิง",
                "รักเรียน",
                "0898765432",
                202
        );

        when(residentRepository.findAll())
                .thenReturn(List.of(resident1, resident2));

        List<AdminResidentResponse> result =
                adminResidentService.getAllResidents();

        assertEquals(2, result.size());

        assertEquals("สมชาย", result.get(0).getFirstName());
        assertEquals("สมหญิง", result.get(1).getFirstName());

        verify(residentRepository, times(1)).findAll();
    }

    // TC-UT-24-02:
    // ตรวจสอบกรณีไม่มีข้อมูลผู้พักอาศัย
    @Test
    void shouldReturnEmptyListWhenNoResidentsExist() {
        when(residentRepository.findAll())
                .thenReturn(List.of());

        List<AdminResidentResponse> result =
                adminResidentService.getAllResidents();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(residentRepository, times(1)).findAll();
    }

    // TC-UT-24-03:
    // ตรวจสอบความถูกต้องของข้อมูลที่แปลงเป็น Response
    @Test
    void shouldMapResidentFieldsCorrectly() {
        UUID residentId = UUID.randomUUID();

        Resident resident = createResident(
                residentId,
                "สมชาย",
                "ใจดี",
                "0812345678",
                101
        );

        when(residentRepository.findAll())
                .thenReturn(List.of(resident));

        List<AdminResidentResponse> result =
                adminResidentService.getAllResidents();

        assertEquals(1, result.size());

        AdminResidentResponse response = result.get(0);

        assertEquals(residentId, response.getResidentId());
        assertEquals("สมชาย", response.getFirstName());
        assertEquals("ใจดี", response.getLastName());
        assertEquals("0812345678", response.getPhoneNo());
        assertEquals(101, response.getRoomNo());
    }

    // TC-UT-24-04:
    // ตรวจสอบว่าระบบส่งต่อ Exception เมื่อ Repository ผิดพลาด
    @Test
    void shouldPropagateExceptionWhenRepositoryFails() {
        when(residentRepository.findAll())
                .thenThrow(new RuntimeException("Repository error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminResidentService.getAllResidents()
        );

        assertEquals("Repository error", exception.getMessage());

        verify(residentRepository, times(1)).findAll();
    }

    private Resident createResident(
            UUID residentId,
            String firstName,
            String lastName,
            String phoneNo,
            int roomNo) {

        Room room = mock(Room.class);
        when(room.getRoomNo()).thenReturn(roomNo);

        Resident resident = mock(Resident.class);
        when(resident.getResidentId()).thenReturn(residentId);
        when(resident.getFirstName()).thenReturn(firstName);
        when(resident.getLastName()).thenReturn(lastName);
        when(resident.getPhoneNo()).thenReturn(phoneNo);
        when(resident.getRoom()).thenReturn(room);

        return resident;
    }
}
