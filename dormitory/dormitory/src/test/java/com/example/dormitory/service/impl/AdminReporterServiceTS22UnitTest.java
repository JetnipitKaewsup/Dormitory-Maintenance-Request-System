
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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.request.AdminReporterUpdateRequest;
import com.example.dormitory.dto.response.AdminReporterResponse;
import com.example.dormitory.repository.AdminReporterRepository;
import com.example.dormitory.repository.RepairRequestRepository;

@ExtendWith(MockitoExtension.class)
class AdminReporterServiceTS22UnitTest {

    @Mock
    private AdminReporterRepository reporterRepository;

    @Mock
    private RepairRequestRepository repairRequestRepository;

    @InjectMocks
    private AdminReporterServiceImpl adminReporterService;

    private UUID reporterId;
    private Reporter reporter;
    private Resident resident;
    private User user;

    @BeforeEach
    void setUp() {
        reporterId = UUID.randomUUID();

        reporter = new Reporter();
        resident = new Resident();
        user = new User();

        reporter.setReporterId(reporterId);
        reporter.setResident(resident);
        reporter.setUser(user);

        resident.setFirstName("Somchai");
        resident.setLastName("Jaidee");
        resident.setPhoneNo("0812345678");

        user.setUserId(UUID.randomUUID());
        user.setUsername("somchai");
        user.setEmail("somchai@example.com");
    }

    // TC-UT-22-01: แก้ไขชื่อ นามสกุล และเบอร์โทรศัพท์สำเร็จ
    @Test
    void TC_UT_22_01_updateReporterInformationSuccessfully() {
        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        AdminReporterUpdateRequest request =
                new AdminReporterUpdateRequest();

        request.setFirstName("Anan");
        request.setLastName("Sukjai");
        request.setPhoneNo("0891234567");

        AdminReporterResponse response =
                adminReporterService.updateReporter(reporterId, request);

        assertNotNull(response);
        assertEquals(reporterId, response.getReporterId());
        assertEquals("Anan", response.getFirstName());
        assertEquals("Sukjai", response.getLastName());
        assertEquals("0891234567", response.getPhoneNo());

        assertEquals("Anan", resident.getFirstName());
        assertEquals("Sukjai", resident.getLastName());
        assertEquals("0891234567", resident.getPhoneNo());

        verify(reporterRepository).save(reporter);
    }

    // TC-UT-22-02: ไม่พบผู้แจ้งตาม ID ที่ระบุ
    @Test
    void TC_UT_22_02_throwExceptionWhenReporterDoesNotExist() {
        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.empty());

        AdminReporterUpdateRequest request =
                new AdminReporterUpdateRequest();

        request.setFirstName("Anan");
        request.setLastName("Sukjai");
        request.setPhoneNo("0891234567");

        assertThrows(
                RuntimeException.class,
                () -> adminReporterService.updateReporter(reporterId, request)
        );

        verify(reporterRepository, never()).save(any(Reporter.class));
    }

    // TC-UT-22-03: ผู้แจ้งไม่มีข้อมูล Resident
    @Test
    void TC_UT_22_03_throwExceptionWhenReporterHasNoResident() {
        reporter.setResident(null);

        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        AdminReporterUpdateRequest request =
                new AdminReporterUpdateRequest();

        request.setFirstName("Anan");
        request.setLastName("Sukjai");
        request.setPhoneNo("0891234567");

        assertThrows(
                RuntimeException.class,
                () -> adminReporterService.updateReporter(reporterId, request)
        );

        verify(reporterRepository, never()).save(any(Reporter.class));
    }

    // TC-UT-22-04: ฟิลด์ที่เป็น null ต้องคงข้อมูลเดิมไว้
    @Test
    void TC_UT_22_04_keepExistingValuesWhenRequestFieldsAreNull() {
        when(reporterRepository.findById(reporterId))
                .thenReturn(Optional.of(reporter));

        AdminReporterUpdateRequest request =
                new AdminReporterUpdateRequest();

        request.setFirstName("Anan");
        request.setLastName(null);
        request.setPhoneNo(null);

        AdminReporterResponse response =
                adminReporterService.updateReporter(reporterId, request);

        assertNotNull(response);

        assertEquals("Anan", resident.getFirstName());
        assertEquals("Jaidee", resident.getLastName());
        assertEquals("0812345678", resident.getPhoneNo());

        assertEquals("Anan", response.getFirstName());
        assertEquals("Jaidee", response.getLastName());
        assertEquals("0812345678", response.getPhoneNo());

        verify(reporterRepository).save(reporter);
    }
}
