package com.example.dormitory.controller.web;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

@ExtendWith(MockitoExtension.class)
class AdminRepairRequestControllerTS09Test {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private RepairAssignmentService repairAssignmentService;

    @Mock
    private Model model;

    private AdminRepairRequestController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminRepairRequestController(
                repairRequestService,
                repairAssignmentService
        );
    }

    /**
     * TC-UT-09-01
     * ตรวจสอบการแสดงรายการแจ้งซ่อมทั้งหมด
     */
    @Test
    void TC_UT_09_01_shouldDisplayAllRepairRequests() {

        RepairRequest pending = new RepairRequest();
        pending.setStatus(RepairRequestStatus.PENDING);

        RepairRequest approved = new RepairRequest();
        approved.setStatus(RepairRequestStatus.APPROVED);

        RepairRequest inProgress = new RepairRequest();
        inProgress.setStatus(RepairRequestStatus.IN_PROGRESS);

        RepairRequest completed = new RepairRequest();
        completed.setStatus(RepairRequestStatus.COMPLETED);

        RepairRequest rejected = new RepairRequest();
        rejected.setStatus(RepairRequestStatus.REJECTED);

        List<RepairRequest> requests = List.of(
                pending,
                approved,
                inProgress,
                completed,
                rejected
        );

        when(repairRequestService.getAllRequests())
                .thenReturn(requests);

        String view = controller.listRequests(model);

        assertEquals("admin/requests-list", view);

        verify(repairRequestService).getAllRequests();

        verify(model).addAttribute("requests", requests);
    }

    /**
     * TC-UT-09-02
     * ตรวจสอบการนับจำนวนคำร้องตามสถานะ
     */
    @Test
    void TC_UT_09_02_shouldCountRequestsByStatus() {

        RepairRequest pending1 = new RepairRequest();
        pending1.setStatus(RepairRequestStatus.PENDING);

        RepairRequest pending2 = new RepairRequest();
        pending2.setStatus(RepairRequestStatus.PENDING);

        RepairRequest approved = new RepairRequest();
        approved.setStatus(RepairRequestStatus.APPROVED);

        RepairRequest inProgress = new RepairRequest();
        inProgress.setStatus(RepairRequestStatus.IN_PROGRESS);

        RepairRequest completed = new RepairRequest();
        completed.setStatus(RepairRequestStatus.COMPLETED);

        RepairRequest rejected = new RepairRequest();
        rejected.setStatus(RepairRequestStatus.REJECTED);

        List<RepairRequest> requests = List.of(
                pending1,
                pending2,
                approved,
                inProgress,
                completed,
                rejected
        );

        when(repairRequestService.getAllRequests())
                .thenReturn(requests);

        controller.listRequests(model);

        verify(model).addAttribute("pendingCount", 2L);
        verify(model).addAttribute("approvedCount", 1L);
        verify(model).addAttribute("inProgressCount", 1L);
        verify(model).addAttribute("completedCount", 1L);
    }

    /**
     * TC-UT-09-03
     * ตรวจสอบการนับจำนวนคำร้องสถานะ REJECTED
     */
    @Test
    void TC_UT_09_03_shouldCountRejectedRequests() {

        RepairRequest rejected1 = new RepairRequest();
        rejected1.setStatus(RepairRequestStatus.REJECTED);

        RepairRequest rejected2 = new RepairRequest();
        rejected2.setStatus(RepairRequestStatus.REJECTED);

        RepairRequest rejected3 = new RepairRequest();
        rejected3.setStatus(RepairRequestStatus.REJECTED);

        List<RepairRequest> requests = List.of(
                rejected1,
                rejected2,
                rejected3
        );

        when(repairRequestService.getAllRequests())
                .thenReturn(requests);

        controller.listRequests(model);

        verify(model).addAttribute("rejectedCount", 3L);
    }

    /**
     * TC-UT-09-04
     * ตรวจสอบกรณีไม่มีรายการแจ้งซ่อม
     */
    @Test
    void TC_UT_09_04_shouldDisplayEmptyListWhenThereAreNoRepairRequests() {

        List<RepairRequest> requests = List.of();

        when(repairRequestService.getAllRequests())
                .thenReturn(requests);

        String view = controller.listRequests(model);

        assertEquals("admin/requests-list", view);

        verify(repairRequestService).getAllRequests();

        verify(model).addAttribute("requests", requests);

        verify(model).addAttribute("pendingCount", 0L);
        verify(model).addAttribute("approvedCount", 0L);
        verify(model).addAttribute("inProgressCount", 0L);
        verify(model).addAttribute("completedCount", 0L);
        verify(model).addAttribute("rejectedCount", 0L);
    }

    /**
     * TC-UT-09-05
     * ตรวจสอบว่า Controller เรียก Service
     * เพื่อดึงรายการแจ้งซ่อมทั้งหมด
     */
    @Test
    void TC_UT_09_05_shouldCallGetAllRequestsOnlyOnce() {

        RepairRequest request = new RepairRequest();
        request.setStatus(RepairRequestStatus.PENDING);

        List<RepairRequest> requests = List.of(request);

        when(repairRequestService.getAllRequests())
                .thenReturn(requests);

        controller.listRequests(model);

        verify(repairRequestService)
                .getAllRequests();
    }
}