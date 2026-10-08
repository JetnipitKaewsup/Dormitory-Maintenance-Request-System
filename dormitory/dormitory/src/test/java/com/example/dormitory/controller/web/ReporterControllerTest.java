package com.example.dormitory.controller.web;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;
@ExtendWith(MockitoExtension.class)
class ReporterControllerTest {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private ReporterProfileService reporterProfileService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private User user;

    @Mock
    private Reporter reporter;

    @InjectMocks
    private ReporterController reporterController;

    private UUID userId;
    private RepairRequestForm form;
    private Model model;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();

        form = new RepairRequestForm();

        model = new ExtendedModelMap();

        when(user.getUserId())
                .thenReturn(userId);
    }

    // =========================================================
    // TC-UT-03-01
    // ตรวจสอบการเปิดหน้าแจ้งซ่อม
    // =========================================================

    @Test
    void TC_UT_03_01_showAddForm_shouldReturnAddRequestPage() {

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
                model.containsAttribute("repairForm"),
                "Model should contain repairForm"
        );

        assertTrue(
                model.containsAttribute("reporter"),
                "Model should contain reporter"
        );

        assertTrue(
                model.containsAttribute("repairTypes"),
                "Model should contain repairTypes"
        );

        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }

    // =========================================================
    // TC-UT-03-02
    // ตรวจสอบการแจ้งซ่อมด้วยข้อมูลที่ถูกต้อง
    // =========================================================

    @Test
    void TC_UT_03_02_createRequest_withValidData_shouldRedirect() {

        when(bindingResult.hasErrors())
                .thenReturn(false);

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
    }

    // =========================================================
    // TC-UT-03-03
    // ตรวจสอบการแจ้งซ่อมเมื่อข้อมูลไม่ถูกต้อง
    // =========================================================

    @Test
    void TC_UT_03_03_createRequest_withBindingErrors_shouldReturnForm() {

        when(bindingResult.hasErrors())
                .thenReturn(true);

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

        assertTrue(
                model.containsAttribute("reporter"),
                "Model should contain reporter"
        );

        assertTrue(
                model.containsAttribute("repairTypes"),
                "Model should contain repairTypes"
        );

        verify(
                repairRequestService,
                never()
        ).createRequest(
                any(UUID.class),
                any(RepairRequestForm.class)
        );
    }

    // =========================================================
    // TC-UT-03-04
    // ตรวจสอบเมื่อ Service พบข้อมูลไม่ถูกต้อง
    // IllegalArgumentException
    // =========================================================

    @Test
    void TC_UT_03_04_createRequest_whenIllegalArgumentException_shouldReturnForm() {

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(
                repairRequestService
                        .createRequest(
                                userId,
                                form
                        )
        ).thenThrow(
                new IllegalArgumentException(
                        "Invalid repair request"
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
                "Invalid repair request",
                model.getAttribute("error")
        );

        assertTrue(
                model.containsAttribute("reporter"),
                "Model should contain reporter"
        );

        assertTrue(
                model.containsAttribute("repairTypes"),
                "Model should contain repairTypes"
        );
    }

    // =========================================================
    // TC-UT-03-05
    // ตรวจสอบเมื่อ Service ไม่สามารถดำเนินการได้
    // IllegalStateException
    // =========================================================

    @Test
    void TC_UT_03_05_createRequest_whenIllegalStateException_shouldReturnForm() {

        when(bindingResult.hasErrors())
                .thenReturn(false);

        when(
                repairRequestService
                        .createRequest(
                                userId,
                                form
                        )
        ).thenThrow(
                new IllegalStateException(
                        "Cannot create repair request"
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
                "Cannot create repair request",
                model.getAttribute("error")
        );

        assertTrue(
                model.containsAttribute("reporter"),
                "Model should contain reporter"
        );

        assertTrue(
                model.containsAttribute("repairTypes"),
                "Model should contain repairTypes"
        );
    }
        // =========================================================
    // TS-04 Unit Test
    // ตรวจสอบการดูประวัติการแจ้งซ่อม
    // =========================================================

    // =========================================================
    // TC-UT-04-01
    // ตรวจสอบการแสดงรายการประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_UT_04_01_showRequests_shouldReturnRequestsPage() {

        RepairRequest request =
                org.mockito.Mockito.mock(RepairRequest.class);

        doReturn(
                java.util.List.of(request)
        ).when(
                repairRequestService
        ).getMyRequests(userId);

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);

        String result =
                reporterController.showRequests(
                        user,
                        model
                );

        assertEquals(
                "reporter/ReporterRequests",
                result
        );

        assertTrue(
                model.containsAttribute("requests"),
                "Model should contain requests"
        );

        assertTrue(
                model.containsAttribute("reporter"),
                "Model should contain reporter"
        );

        verify(
                repairRequestService
        ).getMyRequests(userId);

        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }

    // =========================================================
    // TC-UT-04-02
    // ตรวจสอบเมื่อไม่มีประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_UT_04_02_showRequests_whenNoRequests_shouldReturnRequestsPage() {

        doReturn(
                Collections.emptyList()
        ).when(
                repairRequestService
        ).getMyRequests(userId);

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);

        String result =
                reporterController.showRequests(
                        user,
                        model
                );

        assertEquals(
                "reporter/ReporterRequests",
                result
        );

        assertTrue(
                model.containsAttribute("requests"),
                "Model should contain requests"
        );

        assertTrue(
                model.containsAttribute("reporter"),
                "Model should contain reporter"
        );

        verify(
                repairRequestService
        ).getMyRequests(userId);

        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }

    // =========================================================
    // TC-UT-04-03
    // ตรวจสอบการแสดงรายละเอียดคำร้อง
    // =========================================================

    @Test
    void TC_UT_04_03_showRequestDetail_shouldReturnDetailPage() {

        UUID requestId =
                UUID.randomUUID();

        RepairRequest request =
                org.mockito.Mockito.mock(RepairRequest.class);

        doReturn(request)
                .when(repairRequestService)
                .getMyRequest(
                        userId,
                        requestId
                );

        doReturn(
                Collections.emptyList()
        )
                .when(repairRequestService)
                .getRequestHistory(
                        userId,
                        requestId
                );

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);

        String result =
                reporterController.showRequestDetail(
                        requestId,
                        user,
                        model
                );

        assertEquals(
                "reporter/ReporterRequestDetail",
                result
        );

        assertTrue(
                model.containsAttribute("reporter"),
                "Model should contain reporter"
        );

        assertTrue(
                model.containsAttribute("request"),
                "Model should contain request"
        );

        assertTrue(
                model.containsAttribute("history"),
                "Model should contain history"
        );

        verify(
                repairRequestService
        ).getMyRequest(
                userId,
                requestId
        );

        verify(
                repairRequestService
        ).getRequestHistory(
                userId,
                requestId
        );

        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }

    // =========================================================
    // TC-UT-04-04
    // ตรวจสอบ showRequests เมื่อไม่มี userId
    // =========================================================

    @Test
    void TC_UT_04_04_showRequests_whenUserIdIsNull_shouldRedirectToLogin() {

        when(user.getUserId())
                .thenReturn(null);

        String result =
                reporterController.showRequests(
                        user,
                        model
                );

        assertEquals(
                "redirect:/login",
                result
        );

        verify(
                repairRequestService,
                never()
        ).getMyRequests(any(UUID.class));

        verify(
                reporterProfileService,
                never()
        ).getReporterByUserId(any(UUID.class));
    }

    // =========================================================
    // TC-UT-04-05
    // ตรวจสอบ showRequestDetail เมื่อไม่มี userId
    // =========================================================

    @Test
    void TC_UT_04_05_showRequestDetail_whenUserIdIsNull_shouldRedirectToLogin() {

        UUID requestId =
                UUID.randomUUID();

        when(user.getUserId())
                .thenReturn(null);

        String result =
                reporterController.showRequestDetail(
                        requestId,
                        user,
                        model
                );

        assertEquals(
                "redirect:/login",
                result
        );

        verify(
                repairRequestService,
                never()
        ).getMyRequest(
                any(UUID.class),
                any(UUID.class)
        );

        verify(
                repairRequestService,
                never()
        ).getRequestHistory(
                any(UUID.class),
                any(UUID.class)
        );

        verify(
                reporterProfileService,
                never()
        ).getReporterByUserId(any(UUID.class));
    }
}