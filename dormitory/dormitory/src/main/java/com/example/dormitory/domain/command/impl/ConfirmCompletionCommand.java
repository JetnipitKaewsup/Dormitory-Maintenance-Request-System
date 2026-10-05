package com.example.dormitory.domain.command.impl;

import com.example.dormitory.domain.command.RepairCommand;
import com.example.dormitory.service.RepairAssignmentService;
import com.example.dormitory.service.RepairRequestService;
import java.util.UUID;

public class ConfirmCompletionCommand implements RepairCommand {
    private final RepairRequestService repairRequestService;
    private final RepairAssignmentService repairAssignmentService;
    private final UUID repairRequestId;
    private final UUID adminId;

    public ConfirmCompletionCommand(RepairRequestService repairRequestService,
                                     RepairAssignmentService repairAssignmentService,
                                     UUID repairRequestId, UUID adminId) {
        this.repairRequestService = repairRequestService;
        this.repairAssignmentService = repairAssignmentService;
        this.repairRequestId = repairRequestId;
        this.adminId = adminId;
    }

    @Override
    public void execute() {
        repairRequestService.markCompleted(repairRequestId, adminId);
    }
}