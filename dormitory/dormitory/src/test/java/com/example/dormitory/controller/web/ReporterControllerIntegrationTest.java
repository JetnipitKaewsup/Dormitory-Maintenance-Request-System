package com.example.dormitory.controller.web;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.Resident;
import com.example.dormitory.domain.entity.Room;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairType;
import com.example.dormitory.domain.state.RepairRequestStateRegistry;
import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.event.RepairStatusSubject;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.RepairRequestStatusHistoryRepository;
import com.example.dormitory.repository.ReporterRepository;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;


@SpringBootTest
class ReporterControllerIntegrationTest {

    @Autowired
    private ReporterController reporterController;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private ReporterProfileService reporterProfileService;

    @MockitoBean
    private RepairRequestRepository repairRequestRepository;

    @MockitoBean
    private RepairRequestStatusHistoryRepository historyRepository;

    @MockitoBean
    private ReporterRepository reporterRepository;

    @MockitoBean
    private AdminRepository adminRepository;

    @MockitoBean
    private RepairAssignmentRepository repairAssignmentRepository;

    @MockitoBean
    private RepairStatusSubject repairStatusSubject;

    @MockitoBean
    private RepairRequestStateRegistry repairRequestStateRegistry;


    private UUID userId;

    private User user;

    private Reporter reporter;

    private Resident resident;

    private Room room;

    private Model model;

    private RepairRequestForm form;


    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();

        user = new User();
        user.setUserId(userId);

        room = new Room();
        room.setRoomNo(101);

        resident = new Resident();
        resident.setResidentId(UUID.randomUUID());
        resident.setRoom(room);

        reporter = new Reporter();
        reporter.setReporterId(UUID.randomUUID());
        reporter.setUser(user);
        reporter.setResident(resident);

        model = new ExtendedModelMap();

        form = new RepairRequestForm();

        form.setRepairType(
                RepairType.values()[0].name()
        );

        form.setDescription(
                "ก๊อกน้ำรั่ว"
        );

        form.setPreferredDate(
                LocalDate.now().plusDays(1)
        );

        form.setStartTime(
                LocalTime.of(9, 0)
        );

        form.setEndTime(
                LocalTime.of(10, 0)
        );

        form.setReporterNote(
                "กรุณาเข้าซ่อมช่วงเช้า"
        );
    }


    // =========================================================
    // TC-IT-03-01
    // ตรวจสอบการเปิดหน้าแจ้งซ่อม
    // =========================================================

    @Test
    void TC_IT_03_01_showAddForm_shouldReturnAddRequestPage() {

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);


        String result =
                reporterController.showAddForm(
                        user,
                        model
                );


        assertEquals(
                "reporter/ReporterAddRequest",
                result
        );

        assertTrue(
                model.containsAttribute("repairForm")
        );

        assertTrue(
                model.containsAttribute("reporter")
        );

        assertTrue(
                model.containsAttribute("repairTypes")
        );


        assertEquals(
                reporter,
                model.getAttribute("reporter")
        );


        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }


    // =========================================================
    // TC-IT-03-02
    // ตรวจสอบการแจ้งซ่อมด้วยข้อมูลที่ถูกต้อง
    // =========================================================

    @Test
    void TC_IT_03_02_createRequest_withValidData_shouldRedirect() {

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(
                bindingResult.hasErrors()
        ).thenReturn(false);


        String result =
                reporterController.createRequest(
                        form,
                        bindingResult,
                        user,
                        model
                );


        assertEquals(
                "redirect:/reporter/requests",
                result
        );


        verify(
                repairRequestService
        ).createRequest(
                userId,
                form
        );


        verify(
                repairRequestService,
                times(1)
        ).createRequest(
                userId,
                form
        );
    }


    // =========================================================
    // TC-IT-03-03
    // ตรวจสอบ Validation Error
    // =========================================================

    @Test
    void TC_IT_03_03_createRequest_withValidationError_shouldReturnForm() {

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(
                bindingResult.hasErrors()
        ).thenReturn(true);


        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);


        String result =
                reporterController.createRequest(
                        form,
                        bindingResult,
                        user,
                        model
                );


        assertEquals(
                "reporter/ReporterAddRequest",
                result
        );


        assertEquals(
                reporter,
                model.getAttribute("reporter")
        );


        assertTrue(
                model.containsAttribute("repairTypes")
        );


        verify(
                repairRequestService,
                never()
        ).createRequest(
                any(),
                any()
        );


        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }


    // =========================================================
    // TC-IT-03-04
    // Service throws IllegalArgumentException
    // =========================================================

    @Test
    void TC_IT_03_04_createRequest_whenServiceThrowsIllegalArgumentException_shouldReturnForm() {

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(
                bindingResult.hasErrors()
        ).thenReturn(false);


        when(
                repairRequestService.createRequest(
                        userId,
                        form
                )
        ).thenThrow(
                new IllegalArgumentException(
                        "เวลาสิ้นสุดต้องมากกว่าเวลาเริ่ม"
                )
        );


        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);


        String result =
                reporterController.createRequest(
                        form,
                        bindingResult,
                        user,
                        model
                );


        assertEquals(
                "reporter/ReporterAddRequest",
                result
        );


        assertEquals(
                "เวลาสิ้นสุดต้องมากกว่าเวลาเริ่ม",
                model.getAttribute("error")
        );


        assertEquals(
                reporter,
                model.getAttribute("reporter")
        );


        assertTrue(
                model.containsAttribute("repairTypes")
        );


        verify(
                repairRequestService
        ).createRequest(
                userId,
                form
        );
    }


    // =========================================================
    // TC-IT-03-05
    // Service throws IllegalStateException
    // =========================================================

    @Test
    void TC_IT_03_05_createRequest_whenServiceThrowsIllegalStateException_shouldReturnForm() {

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(
                bindingResult.hasErrors()
        ).thenReturn(false);


        when(
                repairRequestService.createRequest(
                        userId,
                        form
                )
        ).thenThrow(
                new IllegalStateException(
                        "Reporter ไม่ได้เชื่อมกับ Resident"
                )
        );


        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);


        String result =
                reporterController.createRequest(
                        form,
                        bindingResult,
                        user,
                        model
                );


        assertEquals(
                "reporter/ReporterAddRequest",
                result
        );


        assertEquals(
                "Reporter ไม่ได้เชื่อมกับ Resident",
                model.getAttribute("error")
        );


        assertEquals(
                reporter,
                model.getAttribute("reporter")
        );


        assertTrue(
                model.containsAttribute("repairTypes")
        );


        verify(
                repairRequestService
        ).createRequest(
                userId,
                form
        );
    }

    // =========================================================
    // TC-IT-03-06
    // ตรวจสอบการแจ้งซ่อมเมื่อไม่มี User ID
    // =========================================================

    @Test
    void TC_IT_03_06_createRequest_whenUserIdIsNull_shouldRedirectToLogin() {

        User userWithoutId = new User();
        userWithoutId.setUserId(null);

        BindingResult bindingResult =
                mock(BindingResult.class);

        when(
                bindingResult.hasErrors()
        ).thenReturn(false);


        String result =
                reporterController.createRequest(
                        form,
                        bindingResult,
                        userWithoutId,
                        model
                );


        assertEquals(
                "redirect:/login",
                result
        );


        verify(
                repairRequestService,
                never()
        ).createRequest(
                any(),
                any()
        );
    }
}