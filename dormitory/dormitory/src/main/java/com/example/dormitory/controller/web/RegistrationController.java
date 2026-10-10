package com.example.dormitory.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.service.AuthService;

@Controller
public class RegistrationController {

    private static final Logger log =
            LoggerFactory.getLogger(RegistrationController.class);

    private final AuthService authService;

    public RegistrationController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute(
                "registerRequest",
                new RegisterRequest());

        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("registerRequest")
            RegisterRequest registerRequest,
            Model model) {

        try {
            authService.register(registerRequest);

            model.addAttribute(
                    "message",
                    "สมัครสมาชิกสำเร็จ กรุณาตรวจสอบ Email "
                            + "และกดลิงก์เพื่อยืนยันบัญชี");

            return "register-success";

        } catch (Exception e) {
            log.warn(
                    "Registration failed: {}",
                    e.getClass().getSimpleName());

            model.addAttribute(
                    "error",
                    "ไม่สามารถสมัครสมาชิกได้ กรุณาตรวจสอบข้อมูล");

            return "register";
        }
    }

    @GetMapping("/auth/verified")
    public String emailVerified(Model model) {
        model.addAttribute(
                "message",
                "ยืนยัน Email สำเร็จแล้ว");

        return "verified";
    }
}