package com.example.dormitory.dto.adminTechnician;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AdminTechnicianCreateRequest {

    @NotBlank(message = "กรุณากรอกอีเมล")
    @Email(message = "รูปแบบอีเมลไม่ถูกต้อง")
    private String email;

    @NotBlank(message = "กรุณากรอก Username")
    @Size(min = 3, max = 50, message = "Username ต้องมีความยาว 3-50 ตัวอักษร")
    private String username;

    @NotBlank(message = "กรุณากรอกรหัสผ่าน")
    @Size(min = 6, message = "รหัสผ่านต้องมีความยาวอย่างน้อย 6 ตัวอักษร")
    private String password;

    @NotBlank(message = "กรุณากรอกชื่อ")
    private String firstName;

    @NotBlank(message = "กรุณากรอกนามสกุล")
    private String lastName;

    @NotBlank(message = "กรุณากรอกเบอร์โทรศัพท์")
    @Pattern(regexp = "^[0-9]{9,10}$", message = "เบอร์โทรศัพท์ต้องเป็นตัวเลข 9-10 หลัก")
    private String phoneNo;

    @NotBlank(message = "กรุณาเลือกความเชี่ยวชาญ")
    private String specialization;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}