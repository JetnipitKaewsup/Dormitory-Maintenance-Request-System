package com.example.dormitory.domain.command.impl;

import com.example.dormitory.domain.command.RepairCommand;
import com.example.dormitory.service.RepairAssignmentService;

import java.util.UUID;

/** Command: แอดมินมอบหมายงานให้ช่าง (หน้า "มอบหมายงานให้ช่าง") */
public class AssignTechnicianCommand implements RepairCommand {

    private final RepairAssignmentService repairAssignmentService;
    private final UUID repairRequestId;
    private final UUID technicianId;
    private final UUID adminId;
    private final String adminNote;

    public AssignTechnicianCommand(RepairAssignmentService repairAssignmentService,
                                    UUID repairRequestId, UUID technicianId,
                                    UUID adminId, String adminNote) {
        this.repairAssignmentService = repairAssignmentService;
        this.repairRequestId = repairRequestId;
        this.technicianId = technicianId;
        this.adminId = adminId;
        this.adminNote = adminNote;
    }

    @Override
    public void execute() {
        repairAssignmentService.assignTechnician(repairRequestId, technicianId, adminId, adminNote);
    }
}