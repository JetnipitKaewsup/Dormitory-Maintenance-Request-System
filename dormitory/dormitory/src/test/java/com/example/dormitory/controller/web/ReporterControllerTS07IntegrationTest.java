package com.example.dormitory.controller.web;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.RepairRequestStatusHistory;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;

@SpringBootTest
class ReporterControllerTS07IntegrationTest {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private ReporterProfileService reporterProfileService;

    @Mock
    private Model model;

    @Mock
    private User user;

    @Mock
    private Reporter reporter;

    @Mock
    private RepairRequest request;

    @Mock
    private RepairRequestStatusHistory history1;

    @Mock
    private RepairRequestStatusHistory history2;

    private ReporterController controller;

    private UUID userId;
    private UUID requestId;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        controller = new ReporterController(
                repairRequestService,
                reporterProfileService);

        userId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        when(user.getUserId()).thenReturn(userId);
    }

    @Test
    void TC_IT_07_01_shouldShowRequestDetailAndCurrentStatus() {

        List<RepairRequestStatusHistory> history =
                List.of(history1, history2);

        when(reporterProfileService.getReporterByUserId(userId))
                .thenReturn(reporter);

        when(repairRequestService.getMyRequest(userId, requestId))
                .thenReturn(request);

        when(repairRequestService.getRequestHistory(userId, requestId))
                .thenReturn(history);

        String result =
                controller.showRequestDetail(
                        requestId,
                        user,
                        model);

        assertEquals(
                "reporter/ReporterRequestDetail",
                result);

        verify(model)
                .addAttribute("reporter", reporter);

        verify(model)
                .addAttribute("request", request);

        verify(model)
                .addAttribute("history", history);
    }

    @Test
    void TC_IT_07_02_shouldLoadStatusHistory() {

        List<RepairRequestStatusHistory> history =
                List.of(history1, history2);

        when(reporterProfileService.getReporterByUserId(userId))
                .thenReturn(reporter);

        when(repairRequestService.getMyRequest(userId, requestId))
                .thenReturn(request);

        when(repairRequestService.getRequestHistory(userId, requestId))
                .thenReturn(history);

        controller.showRequestDetail(
                requestId,
                user,
                model);

        verify(repairRequestService)
                .getRequestHistory(
                        userId,
                        requestId);

        verify(model)
                .addAttribute(
                        "history",
                        history);
    }

    @Test
    void TC_IT_07_03_shouldUseCorrectUserAndRequestId() {

        List<RepairRequestStatusHistory> history =
                List.of(history1);

        when(reporterProfileService.getReporterByUserId(userId))
                .thenReturn(reporter);

        when(repairRequestService.getMyRequest(
                userId,
                requestId))
                .thenReturn(request);

        when(repairRequestService.getRequestHistory(
                userId,
                requestId))
                .thenReturn(history);

        controller.showRequestDetail(
                requestId,
                user,
                model);

        verify(reporterProfileService)
                .getReporterByUserId(userId);

        verify(repairRequestService)
                .getMyRequest(
                        userId,
                        requestId);

        verify(repairRequestService)
                .getRequestHistory(
                        userId,
                        requestId);
    }

    @Test
    void TC_IT_07_04_shouldRedirectToLoginWhenUserIdIsNull() {

        when(user.getUserId()).thenReturn(null);

        String result =
                controller.showRequestDetail(
                        requestId,
                        user,
                        model);

        assertEquals(
                "redirect:/login",
                result);

        verifyNoInteractions(
                repairRequestService,
                reporterProfileService,
                model);
    }

    @Test
    void TC_IT_07_05_shouldLoadReporterInformation() {

        List<RepairRequestStatusHistory> history =
                List.of(history1);

        when(reporterProfileService.getReporterByUserId(userId))
                .thenReturn(reporter);

        when(repairRequestService.getMyRequest(
                userId,
                requestId))
                .thenReturn(request);

        when(repairRequestService.getRequestHistory(
                userId,
                requestId))
                .thenReturn(history);

        controller.showRequestDetail(
                requestId,
                user,
                model);

        verify(reporterProfileService)
                .getReporterByUserId(userId);

        verify(model)
                .addAttribute(
                        "reporter",
                        reporter);
    }
}