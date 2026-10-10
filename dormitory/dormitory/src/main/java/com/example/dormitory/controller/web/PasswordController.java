package com.example.dormitory.controller.web;

import java.util.Map;

import com.example.dormitory.dto.request.ResetPasswordRequest;
import com.example.dormitory.service.AuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PasswordController {

    private static final Logger log =
            LoggerFactory.getLogger(PasswordController.class);

    private final AuthService authService;

    public PasswordController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/forgot")
    public String forgotPassword() {
        return "forgot";
    }

    @PostMapping("/forgot")
    public String processForgotPassword(
            @RequestParam String email,
            Model model) {

        try {
            authService.forgotPassword(email);

            model.addAttribute(
                    "message",
                    "ส่งลิงก์รีเซ็ตรหัสผ่านไปยัง Email ของคุณแล้ว");

        } catch (Exception e) {
            log.warn(
                    "Forgot-password request failed: {}",
                    e.getClass().getSimpleName());

            // ใช้ข้อความทั่วไป ไม่เปิดเผยรายละเอียดภายใน
            model.addAttribute(
                    "error",
                    "ไม่สามารถดำเนินการได้ กรุณาตรวจสอบข้อมูล");
        }

        return "forgot";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    @PostMapping("/api/v1/auth/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestBody(required = false)
            ResetPasswordRequest request) {

        if (request == null) {
            return badRequest("Request is required");
        }

        if (request.accessToken() == null
                || request.accessToken().isBlank()) {
            return badRequest("Reset token is required");
        }

        if (request.password() == null
                || request.password().isBlank()) {
            return badRequest("Password is required");
        }

        if (request.confirmPassword() == null
                || request.confirmPassword().isBlank()) {
            return badRequest("Password confirmation is required");
        }

        if (!request.password().equals(request.confirmPassword())) {
            return badRequest("Passwords do not match");
        }

        try {
            authService.resetPassword(
                    request.accessToken(),
                    request.password());

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Password reset successfully"
                    ));

        } catch (Exception e) {
            log.warn(
                    "Password reset failed: {}",
                    e.getClass().getSimpleName());

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message",
                            "Unable to reset password"
                    ));
        }
    }

    private ResponseEntity<Map<String, Object>> badRequest(
            String message) {

        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "message", message
                ));
    }
}