package com.example.dormitory.service;

import com.example.dormitory.model.Admin;
import com.example.dormitory.model.RepairRequest;
import com.example.dormitory.model.RepairRequestStatusHistory;
import com.example.dormitory.model.User;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.RepairRequestRepository;
import com.example.dormitory.repository.RepairRequestStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RepairRequestServiceImpl implements RepairRequestService {

    private final RepairRequestRepository repairRequestRepository;
    private final RepairRequestStatusHistoryRepository historyRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public RepairRequestServiceImpl(RepairRequestRepository repairRequestRepository,
                                     RepairRequestStatusHistoryRepository historyRepository,
                                     AdminRepository adminRepository) {
        this.repairRequestRepository = repairRequestRepository;
        this.historyRepository = historyRepository;
        this.adminRepository = adminRepository;
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
            request.setRemark(note);
        }
        repairRequestRepository.save(request);

        User changeByUser = admin.getUser();
        RepairRequestStatusHistory history = new RepairRequestStatusHistory(
                request, changeByUser, newStatus, previousStatus, LocalDateTime.now()
        );
        historyRepository.save(history);
    }
}