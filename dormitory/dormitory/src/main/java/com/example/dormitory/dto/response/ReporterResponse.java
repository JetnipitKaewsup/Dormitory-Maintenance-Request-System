package com.example.dormitory.dto.response;

import com.example.dormitory.domain.entity.Reporter;

import java.util.UUID;

public record ReporterResponse(
        UUID reporterId,
        String firstName,
        String lastName,
        String phoneNo,
        int roomNo,
        int buildingNo
) {
    public static ReporterResponse from(Reporter r) {
        return new ReporterResponse(
                r.getReporterId(),
                r.getUser().getFirstName(),
                r.getUser().getLastName(),
                r.getUser().getPhoneNo(),
                r.getResident() != null && r.getResident().getRoom() != null
                        ? r.getResident().getRoom().getRoomNo() : null,
                r.getResident() != null && r.getResident().getRoom() != null
                        && r.getResident().getRoom().getBuilding() != null
                        ? r.getResident().getRoom().getBuilding().getBuildingNo() : null
        );
    }
}