package com.example.dormitory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AdminResidentCreateRequest {

    @NotBlank(message = "กรุณากรอกชื่อ")
    private String firstName;

    @NotBlank(message = "กรุณากรอกนามสกุล")
    private String lastName;

    @NotBlank(message = "กรุณากรอกเบอร์โทรศัพท์")
    @Pattern(regexp = "^[0-9]{9,10}$", message = "เบอร์โทรศัพท์ต้องเป็นตัวเลข 9-10 หลัก")
    private String phoneNo;

    @NotNull(message = "กรุณาเลือกห้อง")
    private Integer roomNo;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public Integer getRoomNo() { return roomNo; }
    public void setRoomNo(Integer roomNo) { this.roomNo = roomNo; }
}