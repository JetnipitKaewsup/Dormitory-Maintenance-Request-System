package com.example.dormitory.dto.request;

import jakarta.validation.constraints.Pattern;

public class AdminTechnicianUpdateRequest {

    private String firstName;
    private String lastName;

    @Pattern(regexp = "^[0-9]{9,10}$", message = "เบอร์โทรศัพท์ต้องเป็นตัวเลข 9-10 หลัก")
    private String phoneNo;

    private String specialization;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}