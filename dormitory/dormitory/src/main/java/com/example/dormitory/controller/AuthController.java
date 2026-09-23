package com.example.dormitory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.dormitory.dto.LoginRequest;
import com.example.dormitory.dto.RegisterRequest;
import com.example.dormitory.dto.SupabaseAuthResponse;
import com.example.dormitory.service.AuthService;

import jakarta.servlet.http.HttpSession;

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

            return "redirect:/dashboard";

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
        @ModelAttribute("registerRequest") RegisterRequest registerRequest,
        Model model) {

    System.out.println("========== REGISTER ==========");
    System.out.println("Username: " + registerRequest.getUsername());
    System.out.println("Email: " + registerRequest.getEmail());

    System.out.println("Password received: "
            + (registerRequest.getPassword() != null ? "YES" : "NO"));

    System.out.println("Confirm password received: "
            + (registerRequest.getConfirmPassword() != null ? "YES" : "NO"));

    System.out.println("Password length: "
            + (registerRequest.getPassword() != null
                ? registerRequest.getPassword().length()
                : 0));

    System.out.println("Confirm length: "
            + (registerRequest.getConfirmPassword() != null
                ? registerRequest.getConfirmPassword().length()
                : 0));

    // Prevent NullPointerException
    if (registerRequest.getPassword() == null
            || registerRequest.getConfirmPassword() == null) {

        model.addAttribute("error", "Password is required");
        return "register";
    }

    // Check password confirmation
    if (!registerRequest.getPassword()
            .equals(registerRequest.getConfirmPassword())) {

        System.out.println(">>> PASSWORD DOES NOT MATCH");

        model.addAttribute("error", "Passwords do not match");
        return "register";
    }

    System.out.println(">>> PASSWORD MATCHES");

    try {

        authService.register(registerRequest);

        return "redirect:/login";

    } catch (Exception e) {

        e.printStackTrace();

        model.addAttribute(
                "error",
                e.getMessage()
        );

        return "register";
    }
}

// =========================
// DASHBOARD PAGE
// =========================

        @GetMapping("/dashboard")
        public String showDashboard(HttpSession session, Model model) {

            String accessToken =
                    (String) session.getAttribute("accessToken");

            // ถ้ายังไม่ได้ Login
            if (accessToken == null) {
                return "redirect:/login";
            }

            model.addAttribute(
                    "message",
                    "Login successful!"
            );

            return "dashboard";
        }

        @GetMapping("/logout")
public String logout(HttpSession session) {

    session.invalidate();

    return "redirect:/login";
}
}