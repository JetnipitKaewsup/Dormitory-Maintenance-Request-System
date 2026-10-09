package com.example.dormitory.controller.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.dto.response.DailyRepairJobDto;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.TechnicianProfileService;

@ExtendWith(MockitoExtension.class)
class RepairWorkControllerTS29UnitTest {

    @Mock
    private RepairAssignmentService repairAssignmentService;

    @Mock
    private TechnicianProfileService technicianProfileService;

    @Mock
    private User user;

    @Mock
    private Technician technician;

    @Mock
    private DailyRepairSummaryDto summary;

    private RepairWorkController controller;

    private final UUID userId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        controller = new RepairWorkController(
                repairAssignmentService,
                technicianProfileService
        );

        // กำหนดผู้ใช้ที่มี userId สำหรับกรณีปกติ
        when(user.getUserId()).thenReturn(userId);
    }

    /**
     * เตรียมข้อมูลช่างและสรุปงานซ่อม
     * สำหรับกรณีที่ Controller ดำเนินการต่อได้
     */
    private void prepareTechnicianAndSummary() {
        when(technician.getTechnicianId()).thenReturn(technicianId);

        when(technicianProfileService.getTechnicianByUserId(userId))
                .thenReturn(technician);

        when(repairAssignmentService.getDailySummary(technicianId))
                .thenReturn(summary);
    }

    /**
     * TC-UT-29-01
     * ตรวจสอบการแสดงรายการงานซ่อมทั้งหมด
     */
    @Test
    void shouldDisplayAllJobsWhenFilterIsAll() {
        prepareTechnicianAndSummary();

        List<DailyRepairJobDto> jobs =
                List.of(mock(DailyRepairJobDto.class));

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.ALL
        )).thenReturn(jobs);

        Model model = new ExtendedModelMap();

        String result = controller.viewDailyJobs(
                user,
                RepairJobFilter.ALL,
                model
        );

        assertEquals("technician/dailywork", result);
        assertSame(jobs, model.getAttribute("jobs"));
        assertSame(technician, model.getAttribute("technician"));
        assertEquals(technicianId, model.getAttribute("technicianId"));
        assertEquals(RepairJobFilter.ALL, model.getAttribute("filter"));
        assertSame(summary, model.getAttribute("summary"));

        verify(technicianProfileService)
                .getTechnicianByUserId(userId);

        verify(repairAssignmentService)
                .getDailyJobs(technicianId, RepairJobFilter.ALL);

        verify(repairAssignmentService)
                .getDailySummary(technicianId);
    }

    /**
     * TC-UT-29-02
     * ตรวจสอบการกรองงานที่เสร็จสิ้นแล้ว
     */
    @Test
    void shouldFilterCompletedJobs() {
        prepareTechnicianAndSummary();

        List<DailyRepairJobDto> jobs = List.of();

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.COMPLETED
        )).thenReturn(jobs);

        Model model = new ExtendedModelMap();

        String result = controller.viewDailyJobs(
                user,
                RepairJobFilter.COMPLETED,
                model
        );

        assertEquals("technician/dailywork", result);
        assertSame(jobs, model.getAttribute("jobs"));
        assertEquals(
                RepairJobFilter.COMPLETED,
                model.getAttribute("filter")
        );

        verify(technicianProfileService)
                .getTechnicianByUserId(userId);

        verify(repairAssignmentService)
                .getDailyJobs(technicianId, RepairJobFilter.COMPLETED);

        verify(repairAssignmentService)
                .getDailySummary(technicianId);
    }

    /**
     * TC-UT-29-03
     * ตรวจสอบการกรองงานที่กำลังดำเนินการ
     */
    @Test
    void shouldFilterInProgressJobs() {
        prepareTechnicianAndSummary();

        List<DailyRepairJobDto> jobs = List.of();

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.IN_PROGRESS
        )).thenReturn(jobs);

        Model model = new ExtendedModelMap();

        String result = controller.viewDailyJobs(
                user,
                RepairJobFilter.IN_PROGRESS,
                model
        );

        assertEquals("technician/dailywork", result);
        assertSame(jobs, model.getAttribute("jobs"));
        assertEquals(
                RepairJobFilter.IN_PROGRESS,
                model.getAttribute("filter")
        );

        verify(technicianProfileService)
                .getTechnicianByUserId(userId);

        verify(repairAssignmentService)
                .getDailyJobs(technicianId, RepairJobFilter.IN_PROGRESS);

        verify(repairAssignmentService)
                .getDailySummary(technicianId);
    }

    /**
     * TC-UT-29-04
     * ตรวจสอบการกรองงานที่ยังไม่เสร็จสิ้น
     */
    @Test
    void shouldFilterInCompletedJobs() {
        prepareTechnicianAndSummary();

        List<DailyRepairJobDto> jobs = List.of();

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.IN_COMPLETED
        )).thenReturn(jobs);

        Model model = new ExtendedModelMap();

        String result = controller.viewDailyJobs(
                user,
                RepairJobFilter.IN_COMPLETED,
                model
        );

        assertEquals("technician/dailywork", result);
        assertSame(jobs, model.getAttribute("jobs"));
        assertEquals(
                RepairJobFilter.IN_COMPLETED,
                model.getAttribute("filter")
        );

        verify(technicianProfileService)
                .getTechnicianByUserId(userId);

        verify(repairAssignmentService)
                .getDailyJobs(technicianId, RepairJobFilter.IN_COMPLETED);

        verify(repairAssignmentService)
                .getDailySummary(technicianId);
    }

    /**
     * TC-UT-29-05
     * ตรวจสอบการ redirect เมื่อ userId เป็น null
     */
    @Test
    void shouldRedirectToLoginWhenUserIdIsNull() {
        when(user.getUserId()).thenReturn(null);

        Model model = new ExtendedModelMap();

        String result = controller.viewDailyJobs(
                user,
                RepairJobFilter.ALL,
                model
        );

        assertEquals("redirect:/login", result);

        verify(technicianProfileService, never())
                .getTechnicianByUserId(userId);

        verifyNoInteractions(repairAssignmentService);
    }
}
