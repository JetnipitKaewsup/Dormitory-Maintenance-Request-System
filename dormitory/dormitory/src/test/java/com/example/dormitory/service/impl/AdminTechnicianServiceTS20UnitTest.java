package com.example.dormitory.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.repository.AdminTechnicianRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminTechnicianServiceTS20UnitTest {

    @Mock
    private AdminTechnicianRepository technicianRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WebClient supabaseWebClient;

    @Mock
    private RepairAssignmentRepository repairAssignmentRepository;

    @Mock
    private Technician technician;

    @Mock
    private RepairAssignment assignment;

    private AdminTechnicianServiceImpl service;

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        service = new AdminTechnicianServiceImpl(
                technicianRepository,
                userRepository,
                supabaseWebClient,
                repairAssignmentRepository
        );
    }

    // TC-UT-20-01: ดึงประวัติการซ่อมของช่างที่มีรายการ
    @Test
    void TC_UT_20_01_getRepairHistoryWhenTechnicianHasAssignments() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdOrderByAssignDateDesc(technicianId))
                .thenReturn(List.of(assignment));

        var history = service.getRepairHistoryByTechnicianId(technicianId);

        assertNotNull(history);
        assertEquals(1, history.size());

        verify(technicianRepository).findById(technicianId);
        verify(repairAssignmentRepository)
                .findByTechnician_TechnicianIdOrderByAssignDateDesc(technicianId);
    }

    // TC-UT-20-02: ดึงประวัติการซ่อมของช่างที่ไม่มีรายการ
    @Test
    void TC_UT_20_02_getEmptyHistoryWhenTechnicianHasNoAssignments() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(repairAssignmentRepository
                .findByTechnician_TechnicianIdOrderByAssignDateDesc(technicianId))
                .thenReturn(List.of());

        var history = service.getRepairHistoryByTechnicianId(technicianId);

        assertNotNull(history);
        assertEquals(0, history.size());

        verify(technicianRepository).findById(technicianId);
        verify(repairAssignmentRepository)
                .findByTechnician_TechnicianIdOrderByAssignDateDesc(technicianId);
    }

    // TC-UT-20-03: ไม่พบช่างตาม ID ที่ระบุ
    @Test
    void TC_UT_20_03_throwExceptionWhenTechnicianDoesNotExist() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> service.getRepairHistoryByTechnicianId(technicianId)
        );

        verify(technicianRepository).findById(technicianId);

        // เมื่อไม่พบช่าง ต้องไม่ค้นหาประวัติการซ่อม
        verify(repairAssignmentRepository, never())
                .findByTechnician_TechnicianIdOrderByAssignDateDesc(technicianId);
    }
}
