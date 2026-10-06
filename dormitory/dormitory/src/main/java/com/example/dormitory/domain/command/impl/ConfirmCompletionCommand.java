package com.example.dormitory.domain.command.impl;

import com.example.dormitory.domain.command.RepairCommand;
import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairRequestService;

import java.util.UUID;

/**
 * Command: แอดมินกดยืนยันสถานะคำร้อง (หน้า "ตรวจสอบงาน")
 * เปลี่ยน RepairRequest.status -> COMPLETED
 */
public class ConfirmCompletionCommand implements RepairCommand {

    private final RepairRequestService repairRequestService;
    private final UUID repairRequestId;
    private final UUID adminId;
    private final String note;

    public ConfirmCompletionCommand(RepairRequestService repairRequestService,
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
                repairRequestId, adminId, RepairRequestStatus.COMPLETED, note);
    }
}