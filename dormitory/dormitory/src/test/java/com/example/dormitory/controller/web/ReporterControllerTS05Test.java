package com.example.dormitory.controller.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.RepairRequestStatusHistory;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;

class ReporterControllerTS05Test {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private ReporterProfileService reporterProfileService;

    private ReporterController reporterController;

    private UUID userId;
    private UUID requestId;

    private User user;
    private Reporter reporter;
    private RepairRequest request;
    private Model model;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        user = new User();
        user.setUserId(userId);

        reporter = new Reporter();
        reporter.setReporterId(UUID.randomUUID());
        reporter.setUser(user);

        request = new RepairRequest();

        model = new ExtendedModelMap();

        reporterController =
                new ReporterController(
                        repairRequestService,
                        reporterProfileService
                );
    }


    // =========================================================
    // TC-UT-05-01
    // เปิดหน้ารายละเอียดคำร้อง
    // =========================================================

    @Test
    void TC_UT_05_01_showRequestDetail_shouldReturnDetailPage() {

        List<RepairRequestStatusHistory> history =
                Collections.emptyList();

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);

        when(
                repairRequestService
                        .getMyRequest(
                                userId,
                                requestId
                        )
        ).thenReturn(request);

        when(
                repairRequestService
                        .getRequestHistory(
                                userId,
                                requestId
                        )
        ).thenReturn(history);


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
                model.containsAttribute("reporter")
        );

        assertTrue(
                model.containsAttribute("request")
        );

        assertTrue(
                model.containsAttribute("history")
        );
    }


    // =========================================================
    // TC-UT-05-02
    // ตรวจสอบข้อมูลรายละเอียดคำร้อง
    // =========================================================

    @Test
    void TC_UT_05_02_showRequestDetail_shouldAddRequestAndHistoryToModel() {

        RepairRequestStatusHistory historyItem =
                new RepairRequestStatusHistory();

        List<RepairRequestStatusHistory> history =
                List.of(historyItem);

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);

        when(
                repairRequestService
                        .getMyRequest(
                                userId,
                                requestId
                        )
        ).thenReturn(request);

        when(
                repairRequestService
                        .getRequestHistory(
                                userId,
                                requestId
                        )
        ).thenReturn(history);


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

        assertEquals(
                reporter,
                model.getAttribute("reporter")
        );

        assertEquals(
                request,
                model.getAttribute("request")
        );

        assertEquals(
                history,
                model.getAttribute("history")
        );
    }


    // =========================================================
    // TC-UT-05-03
    // ตรวจสอบรายละเอียดคำร้องเมื่อไม่มีประวัติ
    // =========================================================

    @Test
    void TC_UT_05_03_showRequestDetail_whenNoHistory_shouldReturnDetailPage() {

        List<RepairRequestStatusHistory> history =
                Collections.emptyList();

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);

        when(
                repairRequestService
                        .getMyRequest(
                                userId,
                                requestId
                        )
        ).thenReturn(request);

        when(
                repairRequestService
                        .getRequestHistory(
                                userId,
                                requestId
                        )
        ).thenReturn(history);


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

        assertEquals(
                request,
                model.getAttribute("request")
        );

        assertTrue(
                ((List<?>) model.getAttribute("history"))
                        .isEmpty()
        );
    }


    // =========================================================
    // TC-UT-05-04
    // เปิดรายละเอียดเมื่อไม่มี User ID
    // =========================================================

    @Test
    void TC_UT_05_04_showRequestDetail_whenUserIdIsNull_shouldRedirectToLogin() {

        User userWithoutId =
                new User();

        userWithoutId.setUserId(null);


        String result =
                reporterController.showRequestDetail(
                        requestId,
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
        ).getMyRequest(
                any(),
                any()
        );

        verify(
                repairRequestService,
                never()
        ).getRequestHistory(
                any(),
                any()
        );

        verify(
                reporterProfileService,
                never()
        ).getReporterByUserId(
                any()
        );
    }


    // =========================================================
    // TC-UT-05-05
    // ตรวจสอบการเรียก Service
    // =========================================================

    @Test
    void TC_UT_05_05_showRequestDetail_shouldCallRequiredServices() {

        List<RepairRequestStatusHistory> history =
                Collections.emptyList();

        when(
                reporterProfileService
                        .getReporterByUserId(userId)
        ).thenReturn(reporter);

        when(
                repairRequestService
                        .getMyRequest(
                                userId,
                                requestId
                        )
        ).thenReturn(request);

        when(
                repairRequestService
                        .getRequestHistory(
                                userId,
                                requestId
                        )
        ).thenReturn(history);


        reporterController.showRequestDetail(
                requestId,
                user,
                model
        );


        verify(
                reporterProfileService
        ).getReporterByUserId(userId);

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
    }
}