package com.example.dormitory.dto;

import com.example.dormitory.domain.enums.RepairRequestStatus;

import jakarta.validation.constraints.NotNull;


public class UpdateStatusDto {

    @NotNull(message = "กรุณาระบุสถานะ")
    private RepairRequestStatus status;

    private String note;

    public RepairRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RepairRequestStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}