package com.example.dormitory.service.impl;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairAssignmentStatusHistory;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.Technician;

import com.example.dormitory.domain.enums.RepairJobFilter;
import com.example.dormitory.domain.enums.RepairRequestStatus;

import com.example.dormitory.domain.state.RepairRequestStateRegistry;

import com.example.dormitory.dto.response.DailyRepairJobDto;
import com.example.dormitory.dto.response.DailyRepairSummaryDto;

import com.example.dormitory.event.RepairAssignmentCreatedEvent;
import com.example.dormitory.event.RepairAssignmentSubject;
import com.example.dormitory.event.RepairStatusChangedEvent;
import com.example.dormitory.event.RepairStatusSubject;

import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairAssignmentRepository;
import com.example.dormitory.repository.RepairAssignmentStatusHistoryRepository;
import com.example.dormitory.repository.TechnicianRepository;

import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;
import com.example.dormitory.util.DateTimeUtil;

import com.example.dormitory.mapper.DailyRepairJobMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class RepairAssignmentServiceImpl implements RepairAssignmentService {

    private final RepairAssignmentRepository repairAssignmentRepository;
    private final TechnicianRepository technicianRepository;
    private final RepairAssignmentStatusHistoryRepository historyRepository;
    private final AdminRepository adminRepository;
    private final RepairRequestService repairRequestService;
    private final RepairRequestStateRegistry repairRequestStateRegistry;
    private final RepairStatusSubject repairStatusSubject;
    private final RepairAssignmentSubject repairAssignmentSubject;
    private final DailyRepairJobMapper dailyRepairJobMapper;
    
    public RepairAssignmentServiceImpl(
            RepairAssignmentRepository repairAssignmentRepository,
            TechnicianRepository technicianRepository,
            RepairAssignmentStatusHistoryRepository historyRepository,
            AdminRepository adminRepository,
            RepairRequestService repairRequestService,
            RepairRequestStateRegistry repairRequestStateRegistry,
            RepairStatusSubject repairStatusSubject,
            RepairAssignmentSubject repairAssignmentSubject,
            DailyRepairJobMapper dailyRepairJobMapper
    ) {
        this.repairAssignmentRepository = repairAssignmentRepository;
        this.technicianRepository = technicianRepository;
        this.historyRepository = historyRepository;
        this.adminRepository = adminRepository;
        this.repairRequestService = repairRequestService;
        this.repairRequestStateRegistry = repairRequestStateRegistry;
        this.repairStatusSubject = repairStatusSubject;
        this.repairAssignmentSubject = repairAssignmentSubject;
        this.dailyRepairJobMapper = dailyRepairJobMapper;
    }

    // DAILY JOBS
    @Override
    @Transactional(readOnly = true)
    public List<DailyRepairJobDto> getDailyJobs(UUID technicianId) {
        return getDailyJobs(technicianId, RepairJobFilter.ALL);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyRepairJobDto> getDailyJobs(
            UUID technicianId,
            RepairJobFilter filter
    ) {
        getTechnicianOrThrow(technicianId);

        if (filter == null) {
            throw new IllegalArgumentException(
                    "ตัวกรองงานต้องไม่เป็น null"
            );
        }

        List<RepairAssignment> assignments =
                findAssignmentsByFilter(
                        technicianId,
                        filter,
                        getStartOfToday(),
                        getEndOfToday()
                );

        return assignments.stream()
                .map(dailyRepairJobMapper::toDto)
                .toList();
    }

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

        return repairAssignmentRepository
                .findByTechnician_TechnicianIdAndAssignDateBetweenAndJobStatus(
                        technicianId,
                        startOfDay,
                        endOfDay,
                        filter.getStatus()
                );
    }

    // UPDATE JOB STATUS

    @Override
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

        // ป้องกันการแก้ไข Assignment จากช่างคนอื่น
        if (!assignment.getTechnician()
                .getTechnicianId()
                .equals(technicianId)) {
            throw new IllegalStateException(
                    "ช่างไม่มีสิทธิ์แก้ไขงานที่ได้รับมอบหมายให้ช่างคนอื่น"
            );
        }

        RepairRequestStatus previousStatus =
                assignment.getJobStatus();

        if (previousStatus == null || newStatus == null) {
            throw new IllegalArgumentException(
                    "สถานะเดิมและสถานะใหม่ต้องไม่เป็น null"
            );
        }

        if (!repairRequestStateRegistry.canTransition(
                previousStatus,
                newStatus
        )) {
            throw new IllegalStateException(
                    "ไม่สามารถเปลี่ยนสถานะจาก "
                            + previousStatus
                            + " เป็น "
                            + newStatus
            );
        }

        assignment.setJobStatus(newStatus);
        assignment.setTechnicianNote(technicianNote);

        repairAssignmentRepository.save(assignment);

        saveStatusHistory(
                assignment,
                technician,
                previousStatus,
                newStatus
        );

        RepairStatusChangedEvent event =
                new RepairStatusChangedEvent(
                        assignment.getRepairRequest()
                                .getRepairRequestId(),
                        assignment.getAssignmentId(),
                        previousStatus,
                        newStatus,
                        technician.getUser().getUserId(),
                        "TECHNICIAN",
                        technicianNote
                );

        repairStatusSubject.notifyObservers(event);
    }

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
        history.setChangeDate(DateTimeUtil.now());

        historyRepository.save(history);
    }

    // DAILY SUMMARY

    @Override
    @Transactional(readOnly = true)
    public DailyRepairSummaryDto getDailySummary(
            UUID technicianId
    ) {
        getTechnicianOrThrow(technicianId);

        List<RepairAssignment> assignments =
                repairAssignmentRepository
                        .findByTechnician_TechnicianIdAndAssignDateBetween(
                                technicianId,
                                getStartOfToday(),
                                getEndOfToday()
                        );

        long total = assignments.size();

        long completed = countByStatus(
                assignments,
                RepairRequestStatus.COMPLETED
        );

        long inProgress = countByStatus(
                assignments,
                RepairRequestStatus.IN_PROGRESS
        );

        long notCompleted = countByStatus(
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

    // TECHNICIANS - ASSIGNMENTS

    @Override
    @Transactional(readOnly = true)
    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RepairAssignment getAssignmentByRequestId(
            UUID repairRequestId
    ) {
        return repairAssignmentRepository
                .findByRepairRequest_RepairRequestId(repairRequestId)
                .orElse(null);
    }

    @Override
    @Transactional
    public RepairAssignment assignTechnician(
            UUID repairRequestId,
            UUID technicianId,
            UUID adminId,
            String adminNote
    ) {
        RepairRequest repairRequest =
                repairRequestService.getById(repairRequestId);

        Technician technician =
                getTechnicianOrThrow(technicianId);

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบ Admin ID: " + adminId
                        )
                );

        // ตรวจสอบสถานะคำร้องก่อนสร้าง Assignment
        if (repairRequest.getStatus() != RepairRequestStatus.APPROVED) {
            throw new IllegalStateException(
                    "สามารถมอบหมายงานได้เฉพาะคำร้องที่อนุมัติแล้ว"
            );
        }

        if (repairAssignmentRepository
                .findByRepairRequest_RepairRequestId(repairRequestId)
                .isPresent()) {
            throw new IllegalStateException(
                    "คำร้องนี้มีรายการมอบหมายงานแล้ว"
            );
        }

        if (!repairRequestStateRegistry.canTransition(
                repairRequest.getStatus(),
                RepairRequestStatus.IN_PROGRESS
        )) {
            throw new IllegalStateException(
                    "ไม่สามารถเปลี่ยนสถานะคำร้องเป็น IN_PROGRESS ได้"
            );
        }

        RepairAssignment assignment = new RepairAssignment();
        assignment.setRepairRequest(repairRequest);
        assignment.setTechnician(technician);
        assignment.setAdmin(admin);
        assignment.setJobStatus(RepairRequestStatus.IN_PROGRESS);
        assignment.setAdminNote(adminNote);
        assignment.setAssignDate(DateTimeUtil.now());

        RepairAssignment saved =
                repairAssignmentRepository.save(assignment);

        repairRequestService.adminUpdateStatus(
                repairRequestId,
                adminId,
                RepairRequestStatus.IN_PROGRESS,
                adminNote
        );

        RepairAssignmentCreatedEvent event =
                new RepairAssignmentCreatedEvent(
                        saved.getAssignmentId(),
                        repairRequestId,
                        technician.getUser().getUserId(),
                        adminId
                );

        repairAssignmentSubject.notifyObservers(event);

        return saved;
    }
    // COMMON LOOKUPS

    private Technician getTechnicianOrThrow(UUID technicianId) {
        return technicianRepository.findById(technicianId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบช่างเทคนิค ID: " + technicianId
                        )
                );
    }

    private RepairAssignment getAssignmentOrThrow(UUID assignmentId) {
        return repairAssignmentRepository.findById(assignmentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบรายการมอบหมายงาน ID: " + assignmentId
                        )
                );
    }
    // DATE HELPERS

    private LocalDateTime getStartOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    private LocalDateTime getEndOfToday() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }
}