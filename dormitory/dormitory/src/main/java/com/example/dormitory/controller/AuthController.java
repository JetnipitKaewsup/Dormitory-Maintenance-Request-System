package com.example.dormitory.controller;

import com.example.dormitory.dto.LoginRequest;
import com.example.dormitory.dto.RegisterRequest;
import com.example.dormitory.dto.SupabaseAuthResponse;
import com.example.dormitory.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================
    // LOGIN PAGE
    // =========================

    @GetMapping("/login")
    public String showLoginPage(Model model) {

        model.addAttribute(
                "loginRequest",
                new LoginRequest()
        );

        return "login";
    }

    // =========================
    // LOGIN PROCESS
    // =========================

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest,
            HttpSession session,
            Model model) {

        try {

            SupabaseAuthResponse response =
                    authService.login(loginRequest);

            session.setAttribute(
                    "accessToken",
                    response.getAccess_token()
            );

            session.setAttribute(
                    "refreshToken",
                    response.getRefresh_token()
            );

            return "redirect:/";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Invalid email or password"
            );

            return "login";
        }
    }

    // =========================
    // REGISTER PAGE
    // =========================

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "register";
    }

    // =========================
    // REGISTER PROCESS
    // =========================

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute RegisterRequest registerRequest,
            Model model) {

        try {

            authService.register(registerRequest);

            return "redirect:/login";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "register";
        }
    }
}