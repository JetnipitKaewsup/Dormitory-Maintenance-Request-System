package com.example.dormitory.controller.web;

import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.dto.response.DailyRepairJobDto;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.TechnicianProfileService;

@WebMvcTest(RepairWorkController.class)
@AutoConfigureMockMvc(addFilters = false)
class RepairWorkControllerTS29IntegrationTest {

    private static final String URL = "/technician/dailywork";
    private static final String VIEW_NAME = "technician/dailywork";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RepairAssignmentService repairAssignmentService;

    @MockitoBean
    private TechnicianProfileService technicianProfileService;

    private final UUID userId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final UUID technicianId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private Technician technician;
    private DailyRepairSummaryDto summary;
    private UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        // เตรียมผู้ใช้ที่ผ่านการเข้าสู่ระบบ
        // ใช้ User ตัวจริง (ไม่ใช่ mock) เพื่อให้ getUserId() คืนค่าที่ตั้งไว้แน่นอน
        User user = BeanUtils.instantiateClass(User.class);
        ReflectionTestUtils.setField(user, "userId", userId);

        // ตรวจทันทีว่าตั้งค่าสำเร็จ ถ้า fail ตรงนี้แปลว่าชื่อ field ใน User ไม่ใช่ "userId"
        assertEquals(userId, user.getUserId(),
                "User.getUserId() ต้องคืนค่าที่ตั้งไว้");

        // เตรียมข้อมูลช่างซ่อม
        technician = mock(Technician.class);
        when(technician.getTechnicianId()).thenReturn(technicianId);

        when(technicianProfileService.getTechnicianByUserId(userId))
                .thenReturn(technician);

        // เตรียมข้อมูลสรุปงานซ่อม
        summary = mock(DailyRepairSummaryDto.class);
        when(repairAssignmentService.getDailySummary(technicianId))
                .thenReturn(summary);

        // Authentication ที่จะส่งเข้า request (ให้ @AuthenticationPrincipal User อ่านได้)
        authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        "test-password",
                        List.of()
                );
    }

    // สร้าง request พร้อมแนบ Authentication ผ่าน Spring Security Test
    // (การ set SecurityContextHolder เองจะถูก MockMvc เขียนทับเป็นค่าว่างก่อนเข้า controller)
    private MockHttpServletRequestBuilder dailyWork(String filter) {
        return get(URL)
                .param("filter", filter)
                .with(authentication(authentication));
    }

    // ตรวจสอบ model/view ที่ทุกเทสต์ต้องเหมือนกัน และจำนวนครั้งที่เรียก service
    private void performAndVerify(
            RepairJobFilter filter,
            List<DailyRepairJobDto> expectedJobs
    ) throws Exception {

        mockMvc.perform(dailyWork(filter.name()))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_NAME))
                .andExpect(model().attributeExists(
                        "jobs",
                        "technician",
                        "technicianId",
                        "filter",
                        "summary"
                ))
                .andExpect(model().attribute("jobs", expectedJobs))
                .andExpect(model().attribute("technician", technician))
                .andExpect(model().attribute("technicianId", technicianId))
                .andExpect(model().attribute("filter", filter))
                .andExpect(model().attribute("summary", summary));

        verify(technicianProfileService)
                .getTechnicianByUserId(userId);

        verify(repairAssignmentService)
                .getDailyJobs(technicianId, filter);

        verify(repairAssignmentService)
                .getDailySummary(technicianId);

        // ต้องไม่มีการเรียก service เกินที่คาดไว้
        verifyNoMoreInteractions(
                technicianProfileService,
                repairAssignmentService
        );
    }

    /**
     * TC-IT-29-01
     * ตรวจสอบการแสดงรายการงานซ่อมทั้งหมด
     */
    @Test
    void shouldDisplayAllJobsWhenFilterIsAll() throws Exception {
        List<DailyRepairJobDto> jobs =
                List.of(mock(DailyRepairJobDto.class));

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.ALL
        )).thenReturn(jobs);

        performAndVerify(RepairJobFilter.ALL, jobs);
    }

    /**
     * TC-IT-29-02
     * ตรวจสอบการกรองงานที่เสร็จสิ้นแล้ว
     */
    @Test
    void shouldFilterCompletedJobs() throws Exception {
        List<DailyRepairJobDto> jobs =
                List.of(mock(DailyRepairJobDto.class));

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.COMPLETED
        )).thenReturn(jobs);

        performAndVerify(RepairJobFilter.COMPLETED, jobs);
    }

    /**
     * TC-IT-29-03
     * ตรวจสอบการกรองงานที่กำลังดำเนินการ
     */
    @Test
    void shouldFilterInProgressJobs() throws Exception {
        List<DailyRepairJobDto> jobs =
                List.of(mock(DailyRepairJobDto.class));

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.IN_PROGRESS
        )).thenReturn(jobs);

        performAndVerify(RepairJobFilter.IN_PROGRESS, jobs);
    }

    /**
     * TC-IT-29-04
     * ตรวจสอบการกรองงานที่ยังไม่เสร็จสิ้น
     */
    @Test
    void shouldFilterInCompletedJobs() throws Exception {
        List<DailyRepairJobDto> jobs =
                List.of(mock(DailyRepairJobDto.class));

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.IN_COMPLETED
        )).thenReturn(jobs);

        performAndVerify(RepairJobFilter.IN_COMPLETED, jobs);
    }

    /**
     * TC-IT-29-05
     * ตรวจสอบการแสดงรายการว่างเมื่อไม่พบงานที่ตรงกับตัวกรอง
     */
    @Test
    void shouldDisplayEmptyListWhenNoJobsMatchFilter()
            throws Exception {

        List<DailyRepairJobDto> jobs = List.of();

        when(repairAssignmentService.getDailyJobs(
                technicianId,
                RepairJobFilter.COMPLETED
        )).thenReturn(jobs);

        performAndVerify(RepairJobFilter.COMPLETED, jobs);

        // ยืนยันเพิ่มว่า jobs ใน model ว่างจริง
        mockMvc.perform(dailyWork("COMPLETED"))
                .andExpect(model().attribute("jobs", empty()));
    }
}