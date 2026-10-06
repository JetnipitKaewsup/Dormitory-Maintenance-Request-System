package com.example.dormitory.dto.response;

import java.util.UUID;

public class AdminResidentResponse {
    private UUID residentId;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private Integer roomNo;

    public AdminResidentResponse() {
    }

    public AdminResidentResponse(UUID residentId, String firstName, String lastName,
                                  String phoneNo, Integer roomNo) {
        this.residentId = residentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
        this.roomNo = roomNo;
    }

    public UUID getResidentId() { return residentId; }
    public void setResidentId(UUID residentId) { this.residentId = residentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public Integer getRoomNo() { return roomNo; }
    public void setRoomNo(Integer roomNo) { this.roomNo = roomNo; }
}