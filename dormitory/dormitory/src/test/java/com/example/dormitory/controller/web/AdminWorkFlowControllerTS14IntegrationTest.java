
package com.example.dormitory.controller.web;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.ui.ModelMap;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AdminWorkFlowControllerTS14IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepairRequestService repairRequestService;

    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    @BeforeEach
    void setUp() {
        reset(repairRequestService, repairAssignmentService);
    }

    /**
     * เพิ่ม CSRF token เป็น request attribute
     * เพื่อให้ Thymeleaf อ่าน ${_csrf.token} ได้
     */
    private RequestPostProcessor withCsrfRequestAttribute() {
        return request -> {
            CsrfToken csrfToken = new DefaultCsrfToken(
                    "X-CSRF-TOKEN",
                    "_csrf",
                    "test-csrf-token"
            );

            request.setAttribute(CsrfToken.class.getName(), csrfToken);
            request.setAttribute("_csrf", csrfToken);

            return request;
        };
    }

    /**
     * TC-IT-14-01
     * ตรวจสอบว่า HTTP GET /admin/inspections
     * เปิดหน้าแสดงรายการตรวจสอบงานได้ถูกต้อง
     */
    @Test
    void shouldReturnInspectionPageThroughHttpRequest() throws Exception {
        when(repairRequestService.getAllRequests())
                .thenReturn(List.of());

        MvcResult result = mockMvc.perform(
                    get("/admin/inspections")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inspections-list"))
                .andExpect(model().attributeExists(
                        "requests",
                        "readyIds",
                        "readyCount"
                ))
                .andReturn();

        ModelMap model = result.getModelAndView().getModelMap();

        assertNotNull(model.get("requests"));
        assertNotNull(model.get("readyIds"));
        assertEquals(0, model.get("readyCount"));

        verify(repairRequestService, times(1)).getAllRequests();
        verifyNoInteractions(repairAssignmentService);
    }

    /**
     * TC-IT-14-02
     * ตรวจสอบว่าหน้าเว็บแสดงเฉพาะคำร้องสถานะ IN_PROGRESS
     * และเรียกตรวจสอบ assignment เฉพาะคำร้องสถานะนี้
     */
    @Test
    void shouldPassOnlyInProgressRequestsToView() throws Exception {
        UUID inProgressId = UUID.randomUUID();
        UUID approvedId = UUID.randomUUID();
        UUID completedId = UUID.randomUUID();

        RepairRequest inProgressRequest =
                mockRequest(inProgressId, RepairRequestStatus.IN_PROGRESS);

        RepairRequest approvedRequest =
                mockRequest(approvedId, RepairRequestStatus.APPROVED);

        RepairRequest completedRequest =
                mockRequest(completedId, RepairRequestStatus.COMPLETED);

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(
                        inProgressRequest,
                        approvedRequest,
                        completedRequest
                ));

        when(repairAssignmentService.getAssignmentByRequestId(inProgressId))
                .thenReturn(null);

        MvcResult result = mockMvc.perform(
                    get("/admin/inspections")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inspections-list"))
                .andExpect(model().attributeExists(
                        "requests",
                        "readyIds",
                        "readyCount"
                ))
                .andReturn();

        ModelMap model = result.getModelAndView().getModelMap();

        @SuppressWarnings("unchecked")
        List<RepairRequest> requests =
                (List<RepairRequest>) model.get("requests");

        @SuppressWarnings("unchecked")
        Set<UUID> readyIds =
                (Set<UUID>) model.get("readyIds");

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertSame(inProgressRequest, requests.get(0));

        assertNotNull(readyIds);
        assertTrue(readyIds.isEmpty());
        assertEquals(0, model.get("readyCount"));

        verify(repairRequestService, times(1)).getAllRequests();

        verify(repairAssignmentService, times(1))
                .getAssignmentByRequestId(inProgressId);

        verify(repairAssignmentService, never())
                .getAssignmentByRequestId(approvedId);

        verify(repairAssignmentService, never())
                .getAssignmentByRequestId(completedId);
    }

    /**
     * TC-IT-14-03
     * ตรวจสอบว่าคำร้องที่ช่างทำงานเสร็จแล้ว
     * ถูกเพิ่มเข้า readyIds และนับจำนวน readyCount ถูกต้อง
     */
    @Test
    void shouldExposeReadyForInspectionDataToView() throws Exception {
        UUID requestId = UUID.randomUUID();

        RepairRequest request =
                mockRequest(requestId, RepairRequestStatus.IN_PROGRESS);

        RepairAssignment assignment = mock(RepairAssignment.class);

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(request));

        when(repairAssignmentService.getAssignmentByRequestId(requestId))
                .thenReturn(assignment);

        when(assignment.getJobStatus())
                .thenReturn(RepairRequestStatus.COMPLETED);

        MvcResult result = mockMvc.perform(
                    get("/admin/inspections")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inspections-list"))
                .andExpect(model().attributeExists(
                        "requests",
                        "readyIds",
                        "readyCount"
                ))
                .andReturn();

        ModelMap model = result.getModelAndView().getModelMap();

        @SuppressWarnings("unchecked")
        Set<UUID> readyIds =
                (Set<UUID>) model.get("readyIds");

        assertNotNull(readyIds);
        assertTrue(readyIds.contains(requestId));
        assertEquals(1, model.get("readyCount"));

        verify(repairRequestService, times(1)).getAllRequests();

        verify(repairAssignmentService, times(1))
                .getAssignmentByRequestId(requestId);

        // ไม่ตรวจจำนวนครั้งของ getJobStatus()
        // เพราะ Controller อาจเรียก getter นี้มากกว่าหนึ่งครั้ง
    }

    /**
     * TC-IT-14-04
     * ตรวจสอบว่ารายการที่ช่างดำเนินงานเสร็จแล้ว
     * ถูกจัดลำดับไว้ก่อนรายการที่ยังไม่เสร็จ
     */
    @Test
    void shouldLoadAssignmentsAndPrioritizeCompletedJobs()
            throws Exception {

        UUID readyId = UUID.randomUUID();
        UUID pendingId = UUID.randomUUID();

        RepairRequest readyRequest =
                mockRequest(readyId, RepairRequestStatus.IN_PROGRESS);

        RepairRequest pendingRequest =
                mockRequest(pendingId, RepairRequestStatus.IN_PROGRESS);

        RepairAssignment completedAssignment =
                mock(RepairAssignment.class);

        RepairAssignment unfinishedAssignment =
                mock(RepairAssignment.class);

        // ตั้งใจให้รายการที่ยังไม่เสร็จมาก่อน
        // เพื่อทดสอบว่า Controller จัดลำดับใหม่ถูกต้อง
        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(pendingRequest, readyRequest));

        when(repairAssignmentService.getAssignmentByRequestId(readyId))
                .thenReturn(completedAssignment);

        when(completedAssignment.getJobStatus())
                .thenReturn(RepairRequestStatus.COMPLETED);

        when(repairAssignmentService.getAssignmentByRequestId(pendingId))
                .thenReturn(unfinishedAssignment);

        when(unfinishedAssignment.getJobStatus())
                .thenReturn(RepairRequestStatus.IN_PROGRESS);

        MvcResult result = mockMvc.perform(
                    get("/admin/inspections")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inspections-list"))
                .andReturn();

        ModelMap model = result.getModelAndView().getModelMap();

        @SuppressWarnings("unchecked")
        List<RepairRequest> requests =
                (List<RepairRequest>) model.get("requests");

        @SuppressWarnings("unchecked")
        Set<UUID> readyIds =
                (Set<UUID>) model.get("readyIds");

        assertNotNull(requests);
        assertEquals(2, requests.size());

        assertEquals(readyId, requests.get(0).getRepairRequestId());
        assertEquals(pendingId, requests.get(1).getRepairRequestId());

        assertNotNull(readyIds);
        assertTrue(readyIds.contains(readyId));
        assertFalse(readyIds.contains(pendingId));
        assertEquals(1, model.get("readyCount"));

        verify(repairRequestService, times(1)).getAllRequests();

        verify(repairAssignmentService, times(1))
                .getAssignmentByRequestId(readyId);

        verify(repairAssignmentService, times(1))
                .getAssignmentByRequestId(pendingId);
    }

    /**
     * TC-IT-14-05
     * ตรวจสอบกรณีไม่มี assignment หรือ jobStatus เป็น null
     * ระบบต้องไม่ทำให้ HTTP request ล้มเหลว
     * และต้องไม่จัดคำร้องดังกล่าวเป็นงานพร้อมตรวจสอบ
     */
    @Test
    void shouldHandleMissingAssignmentDataWithoutMarkingJobsReady()
            throws Exception {

        UUID noAssignmentId = UUID.randomUUID();
        UUID nullStatusId = UUID.randomUUID();

        RepairRequest noAssignmentRequest =
                mockRequest(
                        noAssignmentId,
                        RepairRequestStatus.IN_PROGRESS
                );

        RepairRequest nullStatusRequest =
                mockRequest(
                        nullStatusId,
                        RepairRequestStatus.IN_PROGRESS
                );

        RepairAssignment nullStatusAssignment =
                mock(RepairAssignment.class);

        when(repairRequestService.getAllRequests())
                .thenReturn(List.of(
                        noAssignmentRequest,
                        nullStatusRequest
                ));

        when(repairAssignmentService.getAssignmentByRequestId(noAssignmentId))
                .thenReturn(null);

        when(repairAssignmentService.getAssignmentByRequestId(nullStatusId))
                .thenReturn(nullStatusAssignment);

        when(nullStatusAssignment.getJobStatus())
                .thenReturn(null);

        MvcResult result = mockMvc.perform(
                    get("/admin/inspections")
                            .with(withCsrfRequestAttribute())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inspections-list"))
                .andExpect(model().attributeExists(
                        "requests",
                        "readyIds",
                        "readyCount"
                ))
                .andReturn();

        ModelMap model = result.getModelAndView().getModelMap();

        @SuppressWarnings("unchecked")
        List<RepairRequest> requests =
                (List<RepairRequest>) model.get("requests");

        @SuppressWarnings("unchecked")
        Set<UUID> readyIds =
                (Set<UUID>) model.get("readyIds");

        assertNotNull(requests);
        assertEquals(2, requests.size());

        assertNotNull(readyIds);
        assertTrue(readyIds.isEmpty());
        assertEquals(0, model.get("readyCount"));

        verify(repairRequestService, times(1)).getAllRequests();

        verify(repairAssignmentService, times(1))
                .getAssignmentByRequestId(noAssignmentId);

        verify(repairAssignmentService, times(1))
                .getAssignmentByRequestId(nullStatusId);
    }

    /**
     * สร้าง Mock RepairRequest สำหรับใช้ใน Integration Test
     */
    private RepairRequest mockRequest(
            UUID requestId,
            RepairRequestStatus status) {

        RepairRequest request = mock(RepairRequest.class);

        when(request.getRepairRequestId()).thenReturn(requestId);
        when(request.getStatus()).thenReturn(status);

        return request;
    }
}
