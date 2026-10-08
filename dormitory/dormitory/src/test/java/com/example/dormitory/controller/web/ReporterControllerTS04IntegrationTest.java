package com.example.dormitory.controller.web;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.RepairRequestStatusHistory;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;


@SpringBootTest
class ReporterControllerTS04IntegrationTest {

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private ReporterProfileService reporterProfileService;

    private ReporterController reporterController;

    private UUID userId;

    private User user;

    private Reporter reporter;

    private Model model;


    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();

        user = new User();
        user.setUserId(userId);

        reporter = new Reporter();
        reporter.setReporterId(UUID.randomUUID());
        reporter.setUser(user);

        model = new ExtendedModelMap();

        reporterController =
                new ReporterController(
                        repairRequestService,
                        reporterProfileService
                );
    }


    // =========================================================
    // TC-IT-04-01
    // ตรวจสอบการเปิดหน้าประวัติการแจ้งซ่อม
    // =========================================================

    @Test
    void TC_IT_04_01_showRequests_shouldReturnRequestsPage() {

        RepairRequest request =
                mock(RepairRequest.class);

        List<RepairRequest> requests =
                List.of(request);

        when(
                repairRequestService
                        .getMyRequests(userId)
        ).thenReturn(requests);

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
                model.containsAttribute("requests")
        );

        assertTrue(
                model.containsAttribute("reporter")
        );


        assertEquals(
                requests,
                model.getAttribute("requests")
        );

        assertEquals(
                reporter,
                model.getAttribute("reporter")
        );


        verify(
                repairRequestService
        ).getMyRequests(userId);

        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }


    // =========================================================
    // TC-IT-04-02
    // ตรวจสอบประวัติการแจ้งซ่อมเมื่อผู้ใช้ไม่มีคำร้อง
    // =========================================================

    @Test
    void TC_IT_04_02_showRequests_whenNoRequests_shouldReturnRequestsPage() {

        when(
                repairRequestService
                        .getMyRequests(userId)
        ).thenReturn(
                Collections.emptyList()
        );

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
                model.containsAttribute("requests")
        );

        assertTrue(
                model.containsAttribute("reporter")
        );


        assertTrue(
                ((List<?>) model.getAttribute("requests"))
                        .isEmpty()
        );


        assertEquals(
                reporter,
                model.getAttribute("reporter")
        );


        verify(
                repairRequestService
        ).getMyRequests(userId);

        verify(
                reporterProfileService
        ).getReporterByUserId(userId);
    }


    // =========================================================
    // TC-IT-04-03
    // ตรวจสอบการเปิดรายละเอียดคำร้องแจ้งซ่อม
    // =========================================================

    @Test
    void TC_IT_04_03_showRequestDetail_shouldReturnDetailPage() {

        UUID requestId =
                UUID.randomUUID();

        RepairRequest request =
                mock(RepairRequest.class);

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


    // =========================================================
    // TC-IT-04-04
    // ตรวจสอบการเปิดหน้าประวัติการแจ้งซ่อมเมื่อไม่มี User ID
    // =========================================================

    @Test
    void TC_IT_04_04_showRequests_whenUserIdIsNull_shouldRedirectToLogin() {

        User userWithoutId =
                new User();

        userWithoutId.setUserId(null);


        String result =
                reporterController.showRequests(
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
        ).getMyRequests(
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
    // TC-IT-04-05
    // ตรวจสอบการเปิดรายละเอียดคำร้องเมื่อไม่มี User ID
    // =========================================================

    @Test
    void TC_IT_04_05_showRequestDetail_whenUserIdIsNull_shouldRedirectToLogin() {

        UUID requestId =
                UUID.randomUUID();

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
}