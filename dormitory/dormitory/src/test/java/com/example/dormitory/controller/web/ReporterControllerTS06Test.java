package com.example.dormitory.controller.web;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

import com.example.dormitory.domain.entity.User;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;

class ReporterControllerTS06Test {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private ReporterProfileService reporterProfileService;

    private ReporterController reporterController;

    private UUID userId;
    private UUID requestId;

    private User user;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        requestId = UUID.randomUUID();

        user = new User();
        user.setUserId(userId);

        reporterController =
                new ReporterController(
                        repairRequestService,
                        reporterProfileService
                );
    }


    @Test
    void TC_UT_06_01_cancelRequest_shouldRedirectToRequestHistory() {

        String result =
                reporterController.cancelRequest(
                        requestId,
                        user
                );

        assertEquals(
                "redirect:/reporter/requests",
                result
        );

        verify(repairRequestService)
                .deleteRequest(userId, requestId);
    }


    @Test
    void TC_UT_06_02_cancelRequest_shouldCallDeleteRequest() {

        reporterController.cancelRequest(
                requestId,
                user
        );

        verify(repairRequestService)
                .deleteRequest(
                        userId,
                        requestId
                );
    }


    @Test
    void TC_UT_06_03_cancelRequest_shouldUseCorrectUserAndRequestId() {

        reporterController.cancelRequest(
                requestId,
                user
        );

        verify(repairRequestService)
                .deleteRequest(
                        userId,
                        requestId
                );
    }


    @Test
    void TC_UT_06_04_cancelRequest_whenUserIdIsNull_shouldRedirectToLogin() {

        User userWithoutId = new User();
        userWithoutId.setUserId(null);

        String result =
                reporterController.cancelRequest(
                        requestId,
                        userWithoutId
                );

        assertEquals(
                "redirect:/login",
                result
        );

        verify(repairRequestService, never())
                .deleteRequest(
                        any(),
                        any()
                );
    }


    @Test
    void TC_UT_06_05_cancelRequest_shouldNotCallDeleteRequestWithWrongData() {

        UUID anotherUserId = UUID.randomUUID();
        UUID anotherRequestId = UUID.randomUUID();

        reporterController.cancelRequest(
                requestId,
                user
        );

        verify(repairRequestService)
                .deleteRequest(
                        userId,
                        requestId
                );

        verify(repairRequestService, never())
                .deleteRequest(
                        anotherUserId,
                        anotherRequestId
                );
    }
}