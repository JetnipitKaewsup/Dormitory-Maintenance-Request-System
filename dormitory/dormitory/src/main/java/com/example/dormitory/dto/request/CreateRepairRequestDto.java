package com.example.dormitory.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateRepairRequestDto {

    @NotBlank(message = "กรุณาเลือกประเภทงานซ่อม")
    private String repairType;

    @NotBlank(message = "กรุณากรอกอาการ / ปัญหา")
    @Size(max = 500, message = "รายละเอียดต้องไม่เกิน 500 ตัวอักษร")
    private String description;

    @NotNull(message = "กรุณาเลือกวันที่")
    @FutureOrPresent(message = "ไม่สามารถเลือกวันที่ในอดีตได้")
    private LocalDate preferredDate;

    @NotNull(message = "กรุณาระบุเวลาเริ่ม")
    private LocalTime startTime;

    @NotNull(message = "กรุณาระบุเวลาสิ้นสุด")
    private LocalTime endTime;

    @Size(max = 300, message = "หมายเหตุต้องไม่เกิน 300 ตัวอักษร")
    private String reporterNote;

    // getters / setters
    public String getRepairType() { return repairType; }
    public void setRepairType(String repairType) { this.repairType = repairType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getPreferredDate() { return preferredDate; }
    public void setPreferredDate(LocalDate preferredDate) { this.preferredDate = preferredDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getReporterNote() { return reporterNote; }
    public void setReporterNote(String reporterNote) { this.reporterNote = reporterNote; }
}