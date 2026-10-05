package com.example.dormitory.dto.response;

import java.util.UUID;

public class AdminReporterResponse {
    private UUID reporterId;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String username;
    private String email;
    private Integer roomNo;

    public AdminReporterResponse() {
    }

    public AdminReporterResponse(UUID reporterId, UUID userId, String firstName, String lastName,
                                  String phoneNo, String username, String email, Integer roomNo) {
        this.reporterId = reporterId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
        this.username = username;
        this.email = email;
        this.roomNo = roomNo;
    }

    public UUID getReporterId() { return reporterId; }
    public void setReporterId(UUID reporterId) { this.reporterId = reporterId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getRoomNo() { return roomNo; }
    public void setRoomNo(Integer roomNo) { this.roomNo = roomNo; }
}