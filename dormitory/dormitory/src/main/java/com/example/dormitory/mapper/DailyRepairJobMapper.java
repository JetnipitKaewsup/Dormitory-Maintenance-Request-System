package com.example.dormitory.mapper;

import com.example.dormitory.domain.entity.RepairAssignment;
import com.example.dormitory.domain.entity.RepairRequest;
import com.example.dormitory.dto.response.DailyRepairJobDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DailyRepairJobMapper {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH.mm");

    public DailyRepairJobDto toDto(RepairAssignment assignment) {
        RepairRequest request = assignment.getRepairRequest();

        return new DailyRepairJobDto(
                assignment.getAssignmentId(),
                formatTime(
                        request.getStartDateTime(),
                        request.getEndDateTime()
                ),
                defaultText(request.getDescription()),
                getBuildingName(request),
                getRoomName(request),
                getReporterPhone(request),
                defaultText(assignment.getAdminNote()),
                assignment.getJobStatus() == null
                        ? "-"
                        : assignment.getJobStatus().name(),
                defaultText(request.getReporterNote())
        );
    }

    private String getBuildingName(RepairRequest request) {
        if (request.getRoom() == null
                || request.getRoom().getBuilding() == null) {
            return "-";
        }

        return defaultText(
                request.getRoom()
                        .getBuilding()
                        .getBuildingName()
        );
    }

    private String getRoomName(RepairRequest request) {
        if (request.getRoom() == null) {
            return "-";
        }

        return "ห้อง " + request.getRoom().getRoomNo();
    }


    private String getReporterPhone(RepairRequest request) {
        if (request.getReporter() == null
                || request.getReporter().getResident() == null) {
            return "-";
        }

        return defaultText(
                request.getReporter()
                        .getResident()
                        .getPhoneNo()
        );
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String formatTime(
            LocalDateTime start,
            LocalDateTime end
    ) {
        if (start == null || end == null) {
            return "-";
        }

        return start.format(TIME_FORMATTER)
                + " - "
                + end.format(TIME_FORMATTER);
    }
}
