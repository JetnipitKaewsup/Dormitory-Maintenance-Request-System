package com.example.dormitory.domain.command.impl;

import com.example.dormitory.domain.command.RepairCommand;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairRequestService;

import java.util.UUID;

/**
 * Command: แอดมินบันทึกว่าคำร้องซ่อมไม่สำเร็จ (หน้า "ตรวจสอบงาน")
 * เปลี่ยน RepairRequest.status -> IN_COMPLETED
 * (ใช้เมื่อคำร้องอนุมัติแล้ว ช่างรับงานแล้ว แต่ทำไม่สำเร็จ)
 */
public class RejectCompletionCommand implements RepairCommand {

    private final RepairRequestService repairRequestService;
    private final UUID repairRequestId;
    private final UUID adminId;
    private final String note;

    public RejectCompletionCommand(RepairRequestService repairRequestService,
                                   UUID repairRequestId,
                                   UUID adminId,
                                   String note) {
        this.repairRequestService = repairRequestService;
        this.repairRequestId = repairRequestId;
        this.adminId = adminId;
        this.note = note;
    }

    @Override
    public void execute() {
        repairRequestService.adminUpdateStatus(
                repairRequestId, adminId, RepairRequestStatus.IN_COMPLETED, note);
    }
}