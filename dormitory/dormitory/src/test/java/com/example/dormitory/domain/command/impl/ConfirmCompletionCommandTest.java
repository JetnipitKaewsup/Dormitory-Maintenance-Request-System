
package com.example.dormitory.domain.command.impl;

import com.example.dormitory.domain.enums.RepairRequestStatus;
import com.example.dormitory.service.RepairRequestService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ConfirmCompletionCommandTest {

    private RepairRequestService repairRequestService;
    private UUID repairRequestId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        repairRequestService = mock(RepairRequestService.class);
        repairRequestId = UUID.randomUUID();
        adminId = UUID.randomUUID();
    }

    // TC-UT-15-01:
    // ตรวจสอบว่า Command ส่งสถานะ COMPLETED ไปยัง Service ถูกต้อง
    @Test
    void shouldConfirmRepairCompletion() {
        String note = "ประสานงานกับผู้แจ้งและยืนยันผลการซ่อมแล้ว";

        ConfirmCompletionCommand command =
                new ConfirmCompletionCommand(
                        repairRequestService,
                        repairRequestId,
                        adminId,
                        note
                );

        command.execute();

        verify(repairRequestService).adminUpdateStatus(
                repairRequestId,
                adminId,
                RepairRequestStatus.COMPLETED,
                note
        );
    }

    // TC-UT-15-02:
    // ตรวจสอบว่า Command รองรับกรณีไม่มีหมายเหตุ
    @Test
    void shouldConfirmRepairCompletionWithoutNote() {
        ConfirmCompletionCommand command =
                new ConfirmCompletionCommand(
                        repairRequestService,
                        repairRequestId,
                        adminId,
                        null
                );

        command.execute();

        verify(repairRequestService).adminUpdateStatus(
                repairRequestId,
                adminId,
                RepairRequestStatus.COMPLETED,
                null
        );
    }
}
