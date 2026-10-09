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
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.request.AdminTechnicianUpdateRequest;
import com.example.dormitory.dto.response.AdminTechnicianResponse;
import com.example.dormitory.repository.AdminTechnicianRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AdminTechnicianServiceTS19UnitTest {

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
    private User user;

    private AdminTechnicianServiceImpl service;

    private AdminTechnicianUpdateRequest request;

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final UUID userId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        service = new AdminTechnicianServiceImpl(
                technicianRepository,
                userRepository,
                supabaseWebClient,
                repairAssignmentRepository
        );

        request = new AdminTechnicianUpdateRequest();
    }

    private void mockExistingTechnician() {
        when(technician.getUser()).thenReturn(user);
        when(technician.getTechnicianId()).thenReturn(technicianId);
        when(user.getUserId()).thenReturn(userId);
        when(user.getUsername()).thenReturn("tech001");
    }

    private void mockSaveRepositories() {
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(technicianRepository.save(any(Technician.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void TC_UT_19_01_updateTechnicianSuccessfully() {
        request.setFirstName("สมชาย");
        request.setLastName("ใจดี");
        request.setPhoneNo("0812345678");
        request.setSpecialization("ระบบไฟฟ้า");

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        mockExistingTechnician();
        mockSaveRepositories();

        when(user.getFirstName()).thenReturn("สมชาย");
        when(user.getLastName()).thenReturn("ใจดี");
        when(user.getPhoneNo()).thenReturn("0812345678");
        when(technician.getSpecialization()).thenReturn("ระบบไฟฟ้า");

        AdminTechnicianResponse response =
                service.updateTechnician(technicianId, request);

        assertNotNull(response);
        assertEquals(technicianId, response.getTechnicianId());
        assertEquals(userId, response.getUserId());
        assertEquals("tech001", response.getUsername());
        assertEquals("สมชาย", response.getFirstName());
        assertEquals("ใจดี", response.getLastName());
        assertEquals("0812345678", response.getPhoneNo());
        assertEquals("ระบบไฟฟ้า", response.getSpecialization());

        verify(user).setFirstName("สมชาย");
        verify(user).setLastName("ใจดี");
        verify(user).setPhoneNo("0812345678");
        verify(technician).setSpecialization("ระบบไฟฟ้า");

        verify(userRepository).save(user);
        verify(technicianRepository).save(technician);
    }

    @Test
    void TC_UT_19_02_updateOnlyProvidedFields() {
        // ส่งเฉพาะเบอร์โทรศัพท์ ฟิลด์อื่นเป็น null
        request.setFirstName(null);
        request.setLastName(null);
        request.setPhoneNo("0898765432");
        request.setSpecialization(null);

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        mockExistingTechnician();
        mockSaveRepositories();

        when(user.getFirstName()).thenReturn("สมชาย");
        when(user.getLastName()).thenReturn("ใจดี");
        when(user.getPhoneNo()).thenReturn("0898765432");
        when(technician.getSpecialization()).thenReturn("ระบบไฟฟ้า");

        AdminTechnicianResponse response =
                service.updateTechnician(technicianId, request);

        assertNotNull(response);
        assertEquals("สมชาย", response.getFirstName());
        assertEquals("ใจดี", response.getLastName());
        assertEquals("0898765432", response.getPhoneNo());
        assertEquals("ระบบไฟฟ้า", response.getSpecialization());

        // ฟิลด์ที่ส่ง null ต้องไม่ถูกแก้ไข
        verify(user, never()).setFirstName(any());
        verify(user, never()).setLastName(any());
        verify(technician, never()).setSpecialization(any());

        // เบอร์โทรศัพท์ต้องถูกแก้ไขและบันทึก
        verify(user).setPhoneNo("0898765432");
        verify(userRepository).save(user);
        verify(technicianRepository).save(technician);
    }

    @Test
    void TC_UT_19_03_throwExceptionWhenTechnicianNotFound() {
        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.updateTechnician(technicianId, request)
        );

        assertNotNull(exception.getMessage());

        // เมื่อไม่พบช่าง ต้องไม่บันทึกข้อมูล
        verify(userRepository, never()).save(any(User.class));
        verify(technicianRepository, never()).save(any(Technician.class));
    }

    @Test
    void TC_UT_19_04_saveBothUserAndTechnicianAfterUpdate() {
        request.setFirstName("วิชัย");
        request.setLastName("ทดสอบ");
        request.setPhoneNo("0861234567");
        request.setSpecialization("ระบบประปา");

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        mockExistingTechnician();
        mockSaveRepositories();

        when(user.getFirstName()).thenReturn("วิชัย");
        when(user.getLastName()).thenReturn("ทดสอบ");
        when(user.getPhoneNo()).thenReturn("0861234567");
        when(technician.getSpecialization()).thenReturn("ระบบประปา");

        AdminTechnicianResponse response =
                service.updateTechnician(technicianId, request);

        assertNotNull(response);

        verify(technicianRepository).findById(technicianId);
        verify(userRepository).save(user);
        verify(technicianRepository).save(technician);

        verify(user).setFirstName("วิชัย");
        verify(user).setLastName("ทดสอบ");
        verify(user).setPhoneNo("0861234567");
        verify(technician).setSpecialization("ระบบประปา");
    }
}
