
package com.example.dormitory.service.impl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.repository.AdminReporterRepository;
import com.example.dormitory.repository.RepairRequestRepository;

@ExtendWith(MockitoExtension.class)
class AdminReporterServiceTS21UnitTest {

    @Mock
    private AdminReporterRepository reporterRepository;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private Reporter reporter;

    @Mock
    private Reporter reporterWithoutResident;

    @Mock
    private Resident resident;

    private AdminReporterServiceImpl service;

    private final UUID reporterId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        service = new AdminReporterServiceImpl(
                reporterRepository,
                repairRequestRepository
        );
    }

    // TC-UT-21-01: ตรวจสอบการดึงรายการผู้แจ้งที่มีข้อมูลผู้พักอาศัย
    @Test
    void TC_UT_21_01_getAllReportersWhenDataExists() {
        when(reporterRepository.findAll())
                .thenReturn(List.of(reporter));

        when(reporter.getResident())
                .thenReturn(resident);

        when(reporter.getReporterId())
                .thenReturn(reporterId);

        var result = service.getAllReporters();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));

        verify(reporterRepository).findAll();
    }

    // TC-UT-21-02: ตรวจสอบเมื่อไม่มีข้อมูลผู้แจ้ง
    @Test
    void TC_UT_21_02_getEmptyListWhenNoReportersExist() {
        when(reporterRepository.findAll())
                .thenReturn(List.of());

        var result = service.getAllReporters();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(reporterRepository).findAll();
    }

    // TC-UT-21-03: ตรวจสอบว่ากรองผู้แจ้งที่ไม่มีข้อมูล Resident
    @Test
    void TC_UT_21_03_excludeReportersWithoutResident() {
        when(reporterRepository.findAll())
                .thenReturn(List.of(reporter, reporterWithoutResident));

        when(reporter.getResident())
                .thenReturn(resident);

        when(reporter.getReporterId())
                .thenReturn(reporterId);

        when(reporterWithoutResident.getResident())
                .thenReturn(null);

        var result = service.getAllReporters();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));

        verify(reporterRepository).findAll();
    }
}
