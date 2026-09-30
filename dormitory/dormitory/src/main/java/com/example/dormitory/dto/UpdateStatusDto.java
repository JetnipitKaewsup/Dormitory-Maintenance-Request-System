package com.example.dormitory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateStatusDto {

    @NotBlank(message = "กรุณาระบุสถานะ")
    @Pattern(regexp = "PENDING|APPROVED|REJECTED|IN_PROGRESS|COMPLETED|IN_COMPLETED|CANCELLED", message = "สถานะไม่ถูกต้อง")
    private String status;

    private String note;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}