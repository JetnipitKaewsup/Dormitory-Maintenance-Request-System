package com.example.dormitory.service;

import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.model.Admin;
import com.example.dormitory.model.RepairRequest;
import com.example.dormitory.model.RepairRequestStatusHistory;
import com.example.dormitory.model.Reporter;
import com.example.dormitory.model.User;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.RepairRequestStatusHistoryRepository;
import com.example.dormitory.repository.ReporterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RepairRequestServiceImpl implements RepairRequestService {

    private final RepairRequestRepository repairRequestRepository;
    private final RepairRequestStatusHistoryRepository historyRepository;
    private final AdminRepository adminRepository;
    private final ReporterRepository reporterRepository;

    @Autowired
    public RepairRequestServiceImpl(RepairRequestRepository repairRequestRepository,
                                     RepairRequestStatusHistoryRepository historyRepository,
                                     AdminRepository adminRepository,
                                    ReporterRepository reporterRepository) {
        this.repairRequestRepository = repairRequestRepository;
        this.historyRepository = historyRepository;
        this.adminRepository = adminRepository;
        this.reporterRepository = reporterRepository;
    }

    @Override
    public RepairRequest getById(UUID id) {
        return repairRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคำร้องแจ้งซ่อม id: " + id));
    }

    @Override
    @Transactional
    public void approve(UUID repairRequestId, UUID adminId) {
        changeStatus(repairRequestId, adminId, "APPROVED", null);
    }

    @Override
    @Transactional
    public void reject(UUID repairRequestId, UUID adminId, String rejectReason) {
        changeStatus(repairRequestId, adminId, "REJECTED", rejectReason);
    }

    // logic กลางที่ approve()/reject() เรียกใช้ร่วมกัน
    // 1) อัปเดตสถานะ + ผูก admin คนที่ทำรายการ ลงใน RepairRequest
    // 2) เขียน record ลง RepairRequestStatusHistory เพื่อเก็บ audit trail
    // ทำใน @Transactional เดียวกัน เพื่อกันกรณี save สำเร็จแค่ครึ่งเดียว
    private void changeStatus(UUID repairRequestId, UUID adminId, String newStatus, String note) {
        RepairRequest request = getById(repairRequestId);
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบ admin id: " + adminId));

        String previousStatus = request.getStatus();

        request.setStatus(newStatus);
        request.setAdmin(admin);
        if (note != null && !note.isBlank()) {
            request.setReporterNote(note);
        }
        repairRequestRepository.save(request);

        User changeByUser = admin.getUser();
        RepairRequestStatusHistory history = new RepairRequestStatusHistory(
                request, changeByUser, newStatus, previousStatus, LocalDateTime.now()
        );
        historyRepository.save(history);
    }

    // Reporter - Create
    @Override
    @Transactional
    public RepairRequest createRequest(
            UUID userId,
            RepairRequestForm form) {

        Reporter reporter = reporterRepository
                .findByUserUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบข้อมูล Reporter"));

        if (reporter.getUser() == null) {
            throw new IllegalStateException(
                    "Reporter ไม่ได้เชื่อมกับ User");
        }

        if (reporter.getResident() == null) {
            throw new IllegalStateException(
                    "Reporter ไม่ได้เชื่อมกับ Resident");
        }

        if (reporter.getResident().getRoom() == null) {
            throw new IllegalStateException(
                    "Resident ไม่ได้ถูกกำหนดห้อง");
        }

        if (form.getRepairType() == null
                || form.getRepairType().isBlank()) {

            throw new IllegalArgumentException(
                    "กรุณาเลือกประเภทงานซ่อม");
        }

        if (form.getDescription() == null
                || form.getDescription().isBlank()) {

            throw new IllegalArgumentException(
                    "กรุณากรอกรายละเอียดอาการหรือปัญหา");
        }

        if (form.getPreferredDate() == null
                || form.getStartTime() == null
                || form.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "กรุณาระบุวันและเวลา");
        }

        LocalDateTime startDateTime =
                LocalDateTime.of(
                        form.getPreferredDate(),
                        form.getStartTime());

        LocalDateTime endDateTime =
                LocalDateTime.of(
                        form.getPreferredDate(),
                        form.getEndTime());

        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException(
                    "เวลาสิ้นสุดต้องมากกว่าเวลาเริ่ม");
        }

        RepairRequest request = new RepairRequest();
        
        // ข้อมูลที่ผู้แจ้งกรอก
        request.setRepairType(form.getRepairType());
        request.setDescription(form.getDescription());
        request.setReporterNote(form.getReporterNote());
        request.setStartDateTime(startDateTime);
        request.setEndDateTime(endDateTime);

        // บันทึกวันที่และเวลาที่สร้างคำร้อง
        request.setCreatedAt(LocalDateTime.now());

        // ข้อมูลจากระบบ ห้ามรับจาก form
        request.setReporter(reporter);
        request.setRoom(reporter.getResident().getRoom());
        request.setStatus("SUBMITTED");

        RepairRequest savedRequest =
                repairRequestRepository.save(request);

        // สร้างประวัติสถานะแรก
        RepairRequestStatusHistory history =
                new RepairRequestStatusHistory(
                        savedRequest,
                        reporter.getUser(),
                        "SUBMITTED",
                        null,
                        LocalDateTime.now());

        historyRepository.save(history);

        return savedRequest;
    }

    // Reporter - History
    @Override
    @Transactional(readOnly = true)
    public List<RepairRequest> getMyRequests(UUID userId) {

        Reporter reporter = reporterRepository
                .findByUserUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบข้อมูล Reporter"));

        return repairRequestRepository
                .findByReporter_ReporterIdOrderByStartDateTimeDesc(
                        reporter.getReporterId());
    }

    // Reporter - Detail
    @Override
    @Transactional(readOnly = true)
    public RepairRequest getMyRequest(
            UUID userId,
            UUID repairRequestId) {

        return repairRequestRepository
                .findByRepairRequestIdAndReporter_User_UserId(
                        repairRequestId,
                        userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบคำร้องแจ้งซ่อม"));
    }

    // Reporter - Latest
    @Override
    @Transactional(readOnly = true)
    public RepairRequest getLatestRequest(UUID userId) {

        Reporter reporter = reporterRepository
                .findByUserUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ไม่พบข้อมูล Reporter"));

        return repairRequestRepository
                .findFirstByReporter_ReporterIdOrderByCreatedAtDesc(
                        reporter.getReporterId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "ยังไม่มีคำร้องแจ้งซ่อม"));
    }

    // Reporter - Status History
    @Override
    @Transactional(readOnly = true)
    public List<RepairRequestStatusHistory> getRequestHistory(
            UUID userId,
            UUID repairRequestId) {

        // ตรวจสอบก่อนว่าคำร้องเป็นของ User คนนี้
        getMyRequest(userId, repairRequestId);

        return historyRepository
                .findByRepairRequest_RepairRequestIdOrderByChangeDateAsc(
                        repairRequestId);
    }

    // Reporter - Cancel
    @Override
    @Transactional
    public void cancelRequest(
            UUID userId,
            UUID repairRequestId) {

        RepairRequest request =
                getMyRequest(userId, repairRequestId);

        // ยกเลิกได้เฉพาะก่อน Admin ดำเนินการ
        if (!"SUBMITTED".equals(request.getStatus())) {
            throw new IllegalStateException(
                    "สามารถยกเลิกได้เฉพาะคำร้องสถานะ SUBMITTED");
        }

        String previousStatus =
                request.getStatus();

        request.setStatus("CANCELLED");

        repairRequestRepository.save(request);

        Reporter reporter =
                request.getReporter();

        RepairRequestStatusHistory history =
                new RepairRequestStatusHistory(
                        request,
                        reporter.getUser(),
                        "CANCELLED",
                        previousStatus,
                        LocalDateTime.now());

        historyRepository.save(history);
    }

    // Reporter - Confirm Completion
    @Override
    @Transactional
    public void confirmCompletion(
            UUID userId,
            UUID repairRequestId) {

        RepairRequest request =
                getMyRequest(userId, repairRequestId);

        /*
         * Technician ทำงานเสร็จแล้ว
         * แต่ RepairRequest ยังเป็น IN_PROGRESS
         * จนกว่า Reporter จะยืนยัน
         */
        if (!"IN_PROGRESS".equals(request.getStatus())) {
            throw new IllegalStateException(
                    "คำร้องยังไม่พร้อมสำหรับการยืนยันงานเสร็จ");
        }

        String previousStatus =
                request.getStatus();

        request.setStatus("COMPLETED");

        repairRequestRepository.save(request);

        Reporter reporter =
                request.getReporter();

        RepairRequestStatusHistory history =
                new RepairRequestStatusHistory(
                        request,
                        reporter.getUser(),
                        "COMPLETED",
                        previousStatus,
                        LocalDateTime.now());

        historyRepository.save(history);
    }

}