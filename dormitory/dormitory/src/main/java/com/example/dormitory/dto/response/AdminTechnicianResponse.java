package com.example.dormitory.dto.response;

import java.util.UUID;

public class AdminTechnicianResponse {
    private UUID technicianId;
    private String specialization;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String username;

    public AdminTechnicianResponse() {
    }

    public AdminTechnicianResponse(UUID technicianId, String specialization, UUID userId,
                                    String firstName, String lastName, String phoneNo, String username) {
        this.technicianId = technicianId;
        this.specialization = specialization;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNo = phoneNo;
        this.username = username;
    }

    public UUID getTechnicianId() { return technicianId; }
    public void setTechnicianId(UUID technicianId) { this.technicianId = technicianId; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

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
}