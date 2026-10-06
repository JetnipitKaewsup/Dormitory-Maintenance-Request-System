package com.example.dormitory.service;

import com.example.dormitory.dto.response.DailyRepairJobDto;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;

import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.state.RepairRequestState;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairAssignmentStatusHistory;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;

import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairAssignmentStatusHistoryRepository;
import com.example.dormitory.repository.TechnicianRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.service.RepairRequestService;

@Service
public class RepairAssignmentService {

    private final RepairAssignmentRepository repairAssignmentRepository;
    private final TechnicianRepository technicianRepository;
    private final RepairAssignmentStatusHistoryRepository historyRepository;
    private final List<RepairRequestState> repairRequestStates;
    private final AdminRepository adminRepository;  
     private final RepairRequestService repairRequestService;  
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH.mm");


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RepairAssignmentService(
            RepairAssignmentRepository repairAssignmentRepository,
            TechnicianRepository technicianRepository,
            RepairAssignmentStatusHistoryRepository historyRepository,
            List<RepairRequestState> repairRequestStates,
            AdminRepository adminRepository,                 
            RepairRequestService repairRequestService   
    ) {
        this.repairAssignmentRepository = repairAssignmentRepository;
        this.technicianRepository = technicianRepository;
        this.historyRepository = historyRepository;
        this.repairRequestStates = repairRequestStates;
        this.adminRepository = adminRepository;
        this.repairRequestService = repairRequestService;
    }

//methodสำหรับ state
private RepairRequestState getState(RepairRequestStatus status) {

    return repairRequestStates.stream()
            .filter(state ->
                    state.getStatus() == status
            )
            .findFirst()
            .orElseThrow(() ->
                    new IllegalStateException(
                            "ไม่พบ State สำหรับสถานะ: " + status
                    )
            );
}
    // =====================================================
    // DAILY JOBS
    // =====================================================

    /**
     * งานทั้งหมดของช่างในวันนี้
     */
    @Transactional(readOnly = true)
    public List<DailyRepairJobDto> getDailyJobs(
            UUID technicianId
    ) {

        return getDailyJobs(
                technicianId,
                RepairJobFilter.ALL
        );
    }


    /**
     * งานวันนี้ตาม Filter
     */
    @Transactional(readOnly = true)
    public List<DailyRepairJobDto> getDailyJobs(
            UUID technicianId,
            RepairJobFilter filter
    ) {

        // ตรวจสอบว่ามีช่างจริง
        getTechnicianOrThrow(technicianId);

        // ช่วงเวลาของวันนี้
        LocalDateTime startOfDay = getStartOfToday();
        LocalDateTime endOfDay = getEndOfToday();

        // ดึงงานตาม Filter
        List<RepairAssignment> assignments =
                findAssignmentsByFilter(
                        technicianId,
                        filter,
                        startOfDay,
                        endOfDay
                );

        // แปลง Entity → DTO
        return assignments.stream()
                .map(this::toDailyRepairJobDto)
                .toList();
    }


    // =====================================================
    // FIND ASSIGNMENTS
    // =====================================================

    /**
     * ดึงงานของช่างวันนี้
     */
    private List<RepairAssignment> getTodayAssignments(
            UUID technicianId
    ) {

        getTechnicianOrThrow(technicianId);

        LocalDateTime startOfDay = getStartOfToday();
        LocalDateTime endOfDay = getEndOfToday();

        return repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetween(
                        technicianId,
                        startOfDay,
                        endOfDay
                );
    }


    /**
     * ดึงงานตาม Filter
     */
    private List<RepairAssignment> findAssignmentsByFilter(
            UUID technicianId,
            RepairJobFilter filter,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    ) {

        if (filter == RepairJobFilter.ALL) {

            return repairAssignmentRepository
                    .findByTechnician_TechnicianIdAndAssignDateBetween(
                            technicianId,
                            startOfDay,
                            endOfDay
                    );
        }

RepairRequestStatus status =
        getStatusFromFilter(filter);

        return repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetweenAndJobStatus(
                        technicianId,
                        startOfDay,
                        endOfDay,
                        status
                );
    }


    /**
     * แปลง Filter → JobStatus
     */
private RepairRequestStatus getStatusFromFilter(
        RepairJobFilter filter
) {

    return switch (filter) {

        case COMPLETED ->
                RepairRequestStatus.COMPLETED;

        case IN_PROGRESS ->
                RepairRequestStatus.IN_PROGRESS;

        case IN_COMPLETED ->
                RepairRequestStatus.IN_COMPLETED;

        case ALL ->
                throw new IllegalArgumentException(
                        "ALL ไม่สามารถแปลงเป็นสถานะได้"
                );
    };
}


    // =====================================================
    // DTO MAPPING
    // =====================================================

    /**
     * แปลง RepairAssignment → DailyRepairJobDto
     */
    private DailyRepairJobDto toDailyRepairJobDto(
            RepairAssignment assignment
    ) {

        RepairRequest request =
                assignment.getRepairRequest();

        String time =
                formatTime(
                        request.getStartDateTime(),
                        request.getEndDateTime()
                );

        String description =
                request.getDescription();

        String building =
                getBuildingName(request);

        String room =
                getRoomName(request);

        String phone =
                getReporterPhone(request);

        String adminNote =
                getTextOrDefault(
                        assignment.getAdminNote()
                );

        String reporterNote =
                getTextOrDefault(
                        request.getReporterNote()
                );

        String status =
                assignment.getJobStatus() != null
                        ? assignment.getJobStatus().name()
                        : "-";

        return new DailyRepairJobDto(
                assignment.getAssignmentId(),
                time,
                description,
                building,
                room,
                phone,
                adminNote,
                status,
                reporterNote
        );
    }


    // =====================================================
    // MAPPING HELPERS
    // =====================================================

    /**
     * อาคาร
     */
    private String getBuildingName(
            RepairRequest request
    ) {

        if (request.getRoom() == null
                || request.getRoom().getBuilding() == null) {

            return "-";
        }

        return getTextOrDefault(
                request.getRoom()
                        .getBuilding()
                        .getBuildingName()
        );
    }


    /**
     * ห้อง
     */
    private String getRoomName(
            RepairRequest request
    ) {

        if (request.getRoom() == null) {
            return "-";
        }

        return "ห้อง "
                + request.getRoom().getRoomNo();
    }


    /**
     * เบอร์โทรผู้แจ้ง
     */
    private String getReporterPhone(
            RepairRequest request
    ) {

        if (request.getReporter() == null
                || request.getReporter().getResident() == null) {

            return "-";
        }

        return getTextOrDefault(
                request.getReporter()
                        .getResident()
                        .getPhoneNo()
        );
    }


    /**
     * ป้องกัน null / blank
     */
    private String getTextOrDefault(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return "-";
        }

        return value;
    }


    /**
     * แปลงเวลา
     */
    private String formatTime(
            LocalDateTime start,
            LocalDateTime end
    ) {

        if (start == null || end == null) {
            return "-";
        }

        return start.format(TIME_FORMATTER)
                + " - "
                + end.format(TIME_FORMATTER);
    }


    // =====================================================
    // UPDATE JOB STATUS
    // =====================================================

    /**
     * ช่างอัปเดตสถานะงาน
     */
@Transactional
public void updateJobStatus(
        UUID assignmentId,
        UUID technicianId,
        RepairRequestStatus newStatus,
        String technicianNote
) {

    RepairAssignment assignment =
            getAssignmentOrThrow(assignmentId);

    Technician technician =
            getTechnicianOrThrow(technicianId);

    RepairRequestStatus previousStatus =
            assignment.getJobStatus();

    // ---------------------------------------------
    // หา State ปัจจุบัน
    // ---------------------------------------------

    RepairRequestState currentState =
            getState(previousStatus);

    // ---------------------------------------------
    // ตรวจสอบว่าสามารถเปลี่ยนสถานะได้หรือไม่
    // ---------------------------------------------

    if (!currentState.getAllowedNext().contains(newStatus)) {

        throw new IllegalStateException(
                "ไม่สามารถเปลี่ยนสถานะจาก "
                        + previousStatus
                        + " เป็น "
                        + newStatus
        );
    }

    // ---------------------------------------------
    // เปลี่ยนสถานะ
    // ---------------------------------------------

    assignment.setJobStatus(newStatus);
    assignment.setTechnicianNote(technicianNote);

    repairAssignmentRepository.save(assignment);

    // ---------------------------------------------
    // บันทึกประวัติ
    // ---------------------------------------------

    saveStatusHistory(
            assignment,
            technician,
            previousStatus,
            newStatus
    );
}
    /**
     * บันทึกประวัติการเปลี่ยนสถานะ
     */
    private void saveStatusHistory(
            RepairAssignment assignment,
            Technician technician,
            RepairRequestStatus previousStatus,
            RepairRequestStatus newStatus
    ) {

        RepairAssignmentStatusHistory history =
                new RepairAssignmentStatusHistory();

        history.setAssignment(assignment);
        history.setChangeBy(technician.getUser());
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setChangeDate(LocalDateTime.now());

        historyRepository.save(history);
    }


    // =====================================================
    // DAILY SUMMARY
    // =====================================================

    /**
     * สรุปจำนวนงานของช่างวันนี้
     */
    @Transactional(readOnly = true)
    public DailyRepairSummaryDto getDailySummary(
            UUID technicianId
    ) {

        List<RepairAssignment> assignments =
                getTodayAssignments(technicianId);

        long total =
                assignments.size();

        long completed =
                countByStatus(
                        assignments,
                        RepairRequestStatus.COMPLETED
                );

        long inProgress =
                countByStatus(
                        assignments,
                        RepairRequestStatus.IN_PROGRESS
                );

        long notCompleted =
                countByStatus(
                        assignments,
                        RepairRequestStatus.IN_COMPLETED
                );

        return new DailyRepairSummaryDto(
                total,
                completed,
                inProgress,
                notCompleted
        );
    }


    /**
     * นับจำนวนงานตาม Status
     */
    private long countByStatus(
            List<RepairAssignment> assignments,
            RepairRequestStatus status
    ) {

        return assignments.stream()
                .filter(assignment ->
                        assignment.getJobStatus() == status
                )
                .count();
    }


    // =====================================================
    // COMMON LOOKUP
    // =====================================================

    /**
     * ค้นหาช่าง
     */
    private Technician getTechnicianOrThrow(
            UUID technicianId
    ) {

        return technicianRepository
                .findById(technicianId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบช่างเทคนิค ID: "
                                        + technicianId
                        )
                );
    }


    /**
     * ค้นหา Assignment
     */
    private RepairAssignment getAssignmentOrThrow(
            UUID assignmentId
    ) {

        return repairAssignmentRepository
                .findById(assignmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบรายการมอบหมายงาน"
                        )
                );
    }


    // =====================================================
    // DATE HELPERS
    // =====================================================

    /**
     * เวลาเริ่มต้นของวันนี้
     */
    private LocalDateTime getStartOfToday() {

        return LocalDate
                .now()
                .atStartOfDay();
    }


    /**
     * เวลาสิ้นสุดของวันนี้
     */
    private LocalDateTime getEndOfToday() {

        return LocalDate
                .now()
                .atTime(LocalTime.MAX);
    }

     // =====================================================
    // ASSIGN TECHNICIAN (มอบหมายงานให้ช่าง)
    // =====================================================

    @Transactional(readOnly = true)
    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RepairAssignment getAssignmentByRequestId(UUID repairRequestId) {
        return repairAssignmentRepository
                .findByRepairRequest_RepairRequestId(repairRequestId)
                .orElse(null);
    }

    @Transactional
    public RepairAssignment assignTechnician(
            UUID repairRequestId,
            UUID technicianId,
            UUID adminId,
            String adminNote
    ) {
        RepairRequest repairRequest = repairRequestService.getById(repairRequestId);
        Technician technician = getTechnicianOrThrow(technicianId);

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบ admin ID: " + adminId));

        RepairAssignment assignment = new RepairAssignment();
        assignment.setRepairRequest(repairRequest);
        assignment.setTechnician(technician);
        assignment.setAdmin(admin);
        assignment.setJobStatus(RepairRequestStatus.IN_PROGRESS);
        assignment.setAdminNote(adminNote);
        assignment.setAssignDate(LocalDateTime.now());

        RepairAssignment saved = repairAssignmentRepository.save(assignment);

        // คำร้องหลัก: APPROVED -> IN_PROGRESS (ผ่าน State pattern ใน RepairRequestService)
        repairRequestService.adminUpdateStatus(
                repairRequestId, adminId, RepairRequestStatus.IN_PROGRESS, adminNote);

        return saved;
    }

}