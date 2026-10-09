package com.example.dormitory.controller.web;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

@ExtendWith(MockitoExtension.class)
class AdminWorkFlowControllerTS14Test {

    @Mock
    private RepairRequestService repairRequestService;

    @Mock
    private RepairAssignmentService repairAssignmentService;

    @InjectMocks
    private AdminWorkflowController adminWorkflowController;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ExtendedModelMap();
    }

    // TC-UT-14-01: ตรวจสอบว่าเปิดหน้าตรวจงานได้ถูกต้อง
    @Test
    void listInspectionsReturnsCorrectViewAndModel() {
        when(repairRequestService.getAllRequests())
                .thenReturn(List.of());

        String view = adminWorkflowController.listInspections(model);

        assertEquals("admin/inspections-list", view);
        assertNotNull(model.getAttribute("requests"));
        assertNotNull(model.getAttribute("readyIds"));
        assertEquals(0, model.getAttribute("readyCount"));

        verify(repairRequestService).getAllRequests();
    }

    // TC-UT-14-02: แสดงเฉพาะคำร้องที่อยู่ระหว่างดำเนินการ
    @Test
    void listInspectionsIncludesOnlyInProgressRequests() {
        RepairRequest inProgressRequest = mock(RepairRequest.class);
        RepairRequest approvedRequest = mock(RepairRequest.class);

        when(inProgressRequest.getRepairRequestId())
                .thenReturn(UUID.randomUUID());
        when(inProgressRequest.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(approvedRequest.getRepairRequestId())
                .thenReturn(UUID.randomUUID());
        when(approvedRequest.getStatus())
                .thenReturn(RepairRequestStatus.APPROVED);

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(approvedRequest, inProgressRequest));

        when(repairAssignmentService.getAssignmentByRequestId(
                inProgressRequest.getRepairRequestId()))
                .thenReturn(null);

        String view = adminWorkflowController.listInspections(model);

        assertEquals("admin/inspections-list", view);

        @SuppressWarnings("unchecked")
        List<RepairRequest> requests =
                (List<RepairRequest>) model.getAttribute("requests");

        assertEquals(1, requests.size());
        assertSame(inProgressRequest, requests.get(0));

        verify(repairAssignmentService)
                .getAssignmentByRequestId(inProgressRequest.getRepairRequestId());
        verify(repairAssignmentService, never())
                .getAssignmentByRequestId(approvedRequest.getRepairRequestId());
    }

    // TC-UT-14-03: งานที่ช่างทำเสร็จแล้วต้องถูกระบุว่าพร้อมตรวจสอบ
    @Test
    void completedTechnicianJobIsMarkedReady() {
        UUID requestId = UUID.randomUUID();

        RepairRequest request = mock(RepairRequest.class);
        RepairAssignment assignment = mock(RepairAssignment.class);

        when(request.getRepairRequestId()).thenReturn(requestId);
        when(request.getStatus()).thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(request));

        when(repairAssignmentService.getAssignmentByRequestId(requestId))
                .thenReturn(assignment);

        when(assignment.getJobStatus())
                .thenReturn(RepairRequestStatus.COMPLETED);

        adminWorkflowController.listInspections(model);

        @SuppressWarnings("unchecked")
        Set<UUID> readyIds = (Set<UUID>) model.getAttribute("readyIds");

        assertTrue(readyIds.contains(requestId));
        assertEquals(1, model.getAttribute("readyCount"));
    }

    // TC-UT-14-04: งานที่พร้อมตรวจสอบต้องอยู่ก่อนงานอื่น
    @Test
    void completedJobsAreSortedFirst() {
        UUID inProgressId = UUID.randomUUID();
        UUID completedId = UUID.randomUUID();

        RepairRequest normalRequest = mock(RepairRequest.class);
        RepairRequest completedRequest = mock(RepairRequest.class);

        RepairAssignment normalAssignment = mock(RepairAssignment.class);
        RepairAssignment completedAssignment = mock(RepairAssignment.class);

        when(normalRequest.getRepairRequestId()).thenReturn(inProgressId);
        when(normalRequest.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(completedRequest.getRepairRequestId()).thenReturn(completedId);
        when(completedRequest.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(normalRequest, completedRequest));

        when(repairAssignmentService.getAssignmentByRequestId(inProgressId))
                .thenReturn(normalAssignment);
        when(normalAssignment.getJobStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(repairAssignmentService.getAssignmentByRequestId(completedId))
                .thenReturn(completedAssignment);
        when(completedAssignment.getJobStatus())
                .thenReturn(RepairRequestStatus.COMPLETED);

        adminWorkflowController.listInspections(model);

        @SuppressWarnings("unchecked")
        List<RepairRequest> requests =
                (List<RepairRequest>) model.getAttribute("requests");

        assertEquals(2, requests.size());
        assertSame(completedRequest, requests.get(0));
        assertSame(normalRequest, requests.get(1));
    }

    // TC-UT-14-05: ไม่มีข้อมูลการมอบหมายหรือสถานะงาน ต้องไม่ระบุว่าพร้อมตรวจ
    @Test
    void missingAssignmentOrJobStatusIsNotMarkedReady() {
        UUID noAssignmentId = UUID.randomUUID();
        UUID noStatusId = UUID.randomUUID();

        RepairRequest requestWithoutAssignment = mock(RepairRequest.class);
        RepairRequest requestWithoutStatus = mock(RepairRequest.class);

        RepairAssignment assignmentWithoutStatus = mock(RepairAssignment.class);

        when(requestWithoutAssignment.getRepairRequestId())
                .thenReturn(noAssignmentId);
        when(requestWithoutAssignment.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(requestWithoutStatus.getRepairRequestId())
                .thenReturn(noStatusId);
        when(requestWithoutStatus.getStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(
                        requestWithoutAssignment,
                        requestWithoutStatus
                ));

        when(repairAssignmentService.getAssignmentByRequestId(noAssignmentId))
                .thenReturn(null);

        when(repairAssignmentService.getAssignmentByRequestId(noStatusId))
                .thenReturn(assignmentWithoutStatus);

        when(assignmentWithoutStatus.getJobStatus()).thenReturn(null);

        adminWorkflowController.listInspections(model);

        @SuppressWarnings("unchecked")
        Set<UUID> readyIds = (Set<UUID>) model.getAttribute("readyIds");

        assertTrue(readyIds.isEmpty());
        assertEquals(0, model.getAttribute("readyCount"));
    }
}