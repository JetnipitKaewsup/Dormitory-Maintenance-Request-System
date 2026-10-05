package com.example.dormitory.service.impl;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.domain.entity.RepairRequestStatusHistory;
import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.domain.enums.RepairType;
import com.example.dormitory.domain.state.RepairRequestStateRegistry;
import com.example.dormitory.dto.RepairRequestForm;
import com.example.dormitory.exception.BusinessException;
import com.example.dormitory.exception.ResourceNotFoundException;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.RepairRequestStatusHistoryRepository;
import com.example.dormitory.repository.ReporterRepository;
import com.example.dormitory.service.RepairRequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

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
        private final RepairRequestStateRegistry repairRequestStateRegistry;

        @Autowired
        public RepairRequestServiceImpl(RepairRequestRepository repairRequestRepository,
                        RepairRequestStatusHistoryRepository historyRepository,
                        AdminRepository adminRepository,
                        ReporterRepository reporterRepository,
                        RepairRequestStateRegistry repairRequestStateRegistry) {
                this.repairRequestRepository = repairRequestRepository;
                this.historyRepository = historyRepository;
                this.adminRepository = adminRepository;
                this.reporterRepository = reporterRepository;
                this.repairRequestStateRegistry = repairRequestStateRegistry;
        }

        @Override
        public RepairRequest getById(UUID id) {
                return repairRequestRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคำร้องแจ้งซ่อม id: " + id));
        }

        @Override
        @Transactional(readOnly = true)
        public List<RepairRequest> getAllRequests() {
                return repairRequestRepository.findAll();
        }

        @Override
        @Transactional
        public void approve(UUID repairRequestId, UUID adminId) {
                changeStatus(repairRequestId, adminId, RepairRequestStatus.APPROVED, null);
        }

        @Override
        @Transactional
        public void reject(UUID repairRequestId, UUID adminId, String rejectReason) {
                changeStatus(repairRequestId, adminId, RepairRequestStatus.REJECTED, rejectReason);
        }

        // logic กลางที่ approve()/reject() เรียกใช้ร่วมกัน
        // 1) อัปเดตสถานะ + ผูก admin คนที่ทำรายการ ลงใน RepairRequest
        // 2) เขียน record ลง RepairRequestStatusHistory เพื่อเก็บ audit trail
        // ทำใน @Transactional เดียวกัน เพื่อกันกรณี save สำเร็จแค่ครึ่งเดียว
        private void changeStatus(UUID repairRequestId, UUID adminId, RepairRequestStatus newStatus, String note) {
                RepairRequest request = getById(repairRequestId);
                Admin admin = adminRepository.findById(adminId)
                                .orElseThrow(() -> new IllegalArgumentException("ไม่พบ admin id: " + adminId));

                RepairRequestStatus previousStatus = request.getStatus();
                
                // State Pattern
                if (!repairRequestStateRegistry.canTransition(previousStatus, newStatus)) {
                        throw new BusinessException(
                                "ไม่สามารถเปลี่ยนจาก " + previousStatus + " เป็น " + newStatus + " ได้");
                }

                request.setStatus(newStatus);
                request.setAdmin(admin);
                if (note != null && !note.isBlank()) {
                        request.setReporterNote(note);
                }
                repairRequestRepository.save(request);

                User changeByUser = admin.getUser();
                RepairRequestStatusHistory history = new RepairRequestStatusHistory(
                                request, changeByUser, newStatus, previousStatus, LocalDateTime.now());
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
                                .orElseThrow(() -> new IllegalArgumentException(
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

                LocalDateTime startDateTime = LocalDateTime.of(
                                form.getPreferredDate(),
                                form.getStartTime());

                LocalDateTime endDateTime = LocalDateTime.of(
                                form.getPreferredDate(),
                                form.getEndTime());

                if (!endDateTime.isAfter(startDateTime)) {
                        throw new IllegalArgumentException(
                                        "เวลาสิ้นสุดต้องมากกว่าเวลาเริ่ม");
                }

                RepairRequest request = new RepairRequest();

                // ข้อมูลที่ผู้แจ้งกรอก
                request.setRepairType(RepairType.valueOf(form.getRepairType()));
                request.setDescription(form.getDescription());
                request.setReporterNote(form.getReporterNote());
                request.setStartDateTime(startDateTime);
                request.setEndDateTime(endDateTime);

                // บันทึกวันที่และเวลาที่สร้างคำร้อง
                request.setCreatedAt(LocalDateTime.now());

                // ข้อมูลจากระบบ
                request.setReporter(reporter);
                request.setRoom(reporter.getResident().getRoom());
                request.setStatus(RepairRequestStatus.PENDING);

                RepairRequest savedRequest = repairRequestRepository.save(request);

                // สร้างประวัติสถานะแรก
                RepairRequestStatusHistory history = new RepairRequestStatusHistory(
                                savedRequest,
                                reporter.getUser(),
                                RepairRequestStatus.PENDING,
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
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "ไม่พบข้อมูล Reporter"));

                return repairRequestRepository
                                .findByReporter_ReporterIdOrderByCreatedAtDesc(
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
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "ไม่พบคำร้องแจ้งซ่อม"));
        }

        // Reporter - Latest
        @Override
        @Transactional(readOnly = true)
        public Optional<RepairRequest> getLatestRequest(UUID userId) {

        
                // ค้นหาข้อมูล Reporter จาก User ID
                Optional<Reporter> reporter =
                        reporterRepository.findByUserUserId(userId);

                // ถ้าไม่พบ Reporter ให้ถือว่าไม่มีข้อมูล
                if (reporter.isEmpty()) {
                        return Optional.empty();
                }

                // ค้นหาคำร้องล่าสุดของ Reporter
                return repairRequestRepository
                        .findFirstByReporter_ReporterIdOrderByCreatedAtDesc(
                                reporter.get().getReporterId());
                        
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

                RepairRequest request = getMyRequest(userId, repairRequestId);
                RepairRequestStatus current = request.getStatus();
                
                // ยกเลิกได้เฉพาะก่อน Admin ดำเนินการ (PENDING)
                if (!repairRequestStateRegistry.canTransition(current, RepairRequestStatus.CANCELLED)) {
                        throw new BusinessException(
                                        "สามารถยกเลิกได้เฉพาะคำร้องสถานะ PENDING");
                }

                RepairRequestStatus previousStatus = request.getStatus();

                request.setStatus(RepairRequestStatus.CANCELLED);

                repairRequestRepository.save(request);

                Reporter reporter = request.getReporter();

                RepairRequestStatusHistory history = new RepairRequestStatusHistory(
                                request,
                                reporter.getUser(),
                                RepairRequestStatus.CANCELLED,
                                previousStatus,
                                LocalDateTime.now());

                historyRepository.save(history);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<RepairRequest> getMyRequests(UUID userId, Pageable pageable) {
                Reporter reporter = reporterRepository.findByUserUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบข้อมูล Reporter"));
                return repairRequestRepository.findByReporter_ReporterId(
                                reporter.getReporterId(), pageable);
        }

        

        @Override
        @Transactional
        public void updateStatus(UUID userId, UUID requestId,
                        RepairRequestStatus newStatus, String note) {
                RepairRequest request = getMyRequest(userId, requestId);
                RepairRequestStatus current = request.getStatus();

                if (!repairRequestStateRegistry.canTransition(current,newStatus)) {
                        throw new BusinessException(
                                        "ไม่สามารถเปลี่ยนจาก " + current + " เป็น " + newStatus + " ได้");
                }

                RepairRequestStatus previousStatus = current;
                request.setStatus(newStatus);

                if (note != null && !note.isBlank()) {
                        request.setReporterNote(note);
                }
                repairRequestRepository.save(request);

                RepairRequestStatusHistory history = new RepairRequestStatusHistory(
                                request, request.getReporter().getUser(),
                                newStatus, previousStatus, LocalDateTime.now());
                historyRepository.save(history);
        }

}