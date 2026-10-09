package com.example.dormitory.controller.web;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.dormitory.domain.entity.User;
import com.example.dormitory.exception.BusinessException;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.service.ReporterProfileService;

@SpringBootTest
class ReporterControllerTS06IntegrationTest {

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
    void TC_IT_06_01_cancelRequest_shouldDeletePendingRequest() {

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
                .deleteRequest(
                        userId,
                        requestId
                );
    }


    @Test
    void TC_IT_06_02_cancelRequest_shouldCallServiceWithCorrectIds() {

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
    void TC_IT_06_03_cancelRequest_whenServiceRejectsNonPendingRequest() {

        doThrow(
                new BusinessException(
                        "ลบได้เฉพาะคำร้องที่อยู่ในสถานะ PENDING เท่านั้น"
                )
        ).when(repairRequestService)
                .deleteRequest(
                        userId,
                        requestId
                );

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> reporterController.cancelRequest(
                                requestId,
                                user
                        )
                );

        assertEquals(
                "ลบได้เฉพาะคำร้องที่อยู่ในสถานะ PENDING เท่านั้น",
                exception.getMessage()
        );

        verify(repairRequestService)
                .deleteRequest(
                        userId,
                        requestId
                );
    }


    @Test
    void TC_IT_06_04_cancelRequest_whenUserIdIsNull_shouldRedirectToLogin() {

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

        verify(
                repairRequestService,
                never()
        ).deleteRequest(
                any(),
                any()
        );
    }


    @Test
    void TC_IT_06_05_cancelRequest_whenServiceRejectsAssignedRequest() {

        doThrow(
                new BusinessException(
                        "ไม่สามารถลบได้ เนื่องจากคำร้องถูกมอบหมายให้ช่างแล้ว"
                )
        ).when(repairRequestService)
                .deleteRequest(
                        userId,
                        requestId
                );

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> reporterController.cancelRequest(
                                requestId,
                                user
                        )
                );

        assertEquals(
                "ไม่สามารถลบได้ เนื่องจากคำร้องถูกมอบหมายให้ช่างแล้ว",
                exception.getMessage()
        );

        verify(repairRequestService)
                .deleteRequest(
                        userId,
                        requestId
                );
    }
}