package com.example.dormitory.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.LoginSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    private static final Logger log =
            LoggerFactory.getLogger(LoginController.class);

    private final AuthService authService;
    private final LoginSessionService loginSessionService;

    public LoginController(
            AuthService authService,
            LoginSessionService loginSessionService) {
        this.authService = authService;
        this.loginSessionService = loginSessionService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        try {
            SupabaseAuthResponse authResponse =
                    authService.login(loginRequest);

            String destination =
                    loginSessionService.establishSession(
                            authResponse,
                            request,
                            response);

            return "redirect:" + destination;

        } catch (Exception e) {
            // ไม่บันทึก Password หรือ Token ลง Log
            log.warn("Login failed: {}", e.getClass().getSimpleName());

            model.addAttribute(
                    "error",
                    "Invalid email or password");

            return "login";
        }
    }
}