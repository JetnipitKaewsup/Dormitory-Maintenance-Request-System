package com.example.dormitory.controller.web;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.SpringSecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final AuthService authService;
    private final SpringSecurityService springSecurityService;

    public AuthController(
            AuthService authService,
            SpringSecurityService springSecurityService) {

        this.authService = authService;
        this.springSecurityService = springSecurityService;
    }

    // ============================================================
    // LOGIN PAGE
    // ============================================================

    @GetMapping("/login")
    public String showLoginPage(Model model) {

        model.addAttribute(
                "loginRequest",
                new LoginRequest()
        );

        return "login";
    }

    // ============================================================
    // LOGIN PROCESS
    // ============================================================

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        try {

            // ----------------------------------------------------
            // Login กับ Supabase
            // ----------------------------------------------------

            SupabaseAuthResponse authResponse =
                    authService.login(loginRequest);

            // ----------------------------------------------------
            // Get User ID
            // ----------------------------------------------------

            UUID userId =
                    UUID.fromString(
                            authResponse.getUser().getId()
                    );

            // ----------------------------------------------------
            // Create Spring Security Authentication
            // ----------------------------------------------------

            Authentication authentication =
                    springSecurityService.createAuthentication(userId);

            // ----------------------------------------------------
            // Create Security Context
            // ----------------------------------------------------

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);

            // ----------------------------------------------------
            // Save Security Context to Session
            // ----------------------------------------------------

            HttpSession session =
                    request.getSession();

            HttpSessionSecurityContextRepository
                    securityContextRepository =
                    new HttpSessionSecurityContextRepository();

            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );

            // ----------------------------------------------------
            // Save Supabase Access Token
            // ----------------------------------------------------

            session.setAttribute(
                    "accessToken",
                    authResponse.getAccess_token()
            );

            // ----------------------------------------------------
            // Save Supabase Refresh Token
            // ----------------------------------------------------

            session.setAttribute(
                    "refreshToken",
                    authResponse.getRefresh_token()
            );

            // ----------------------------------------------------
            // Save User ID
            // ----------------------------------------------------

            session.setAttribute(
                    "userId",
                    authResponse.getUser().getId()
            );

            // ----------------------------------------------------
            // Redirect ตาม Role
            // ----------------------------------------------------

            String role =
                    authentication
                            .getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority();

            if (role.equals("ROLE_ADMIN")) {

                return "redirect:/admin/requests";
            }

            if (role.equals("ROLE_TECHNICIAN")) {

                return "redirect:/technician/dailywork";
            }

            if (role.equals("ROLE_REPORTER")) {

                return "redirect:/reporter/requests";
            }

            // ----------------------------------------------------
            // Default
            // ----------------------------------------------------

            return "redirect:/reporter/requests";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Invalid email or password"
            );

            return "login";
        }
    }

    // ============================================================
    // REGISTER PAGE
    // ============================================================

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "register";
    }

    // ============================================================
    // REGISTER PROCESS
    // ============================================================

    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("registerRequest")
            RegisterRequest registerRequest,
            Model model) {

        System.out.println(
                "========== REGISTER =========="
        );

        System.out.println(
                "Username: "
                        + registerRequest.getUsername()
        );

        System.out.println(
                "Email: "
                        + registerRequest.getEmail()
        );

        System.out.println(
                "Password received: "
                        + (registerRequest.getPassword() != null
                        ? "YES"
                        : "NO")
        );

        System.out.println(
                "Confirm password received: "
                        + (registerRequest.getConfirmPassword() != null
                        ? "YES"
                        : "NO")
        );

        System.out.println(
                "Password length: "
                        + (registerRequest.getPassword() != null
                        ? registerRequest.getPassword().length()
                        : 0)
        );

        System.out.println(
                "Confirm length: "
                        + (registerRequest.getConfirmPassword() != null
                        ? registerRequest.getConfirmPassword().length()
                        : 0)
        );

        try {

            // ----------------------------------------------------
            // Register ผ่าน Supabase
            // ----------------------------------------------------

            authService.register(registerRequest);

            // ----------------------------------------------------
            // แสดงหน้าแจ้งเตือนให้ User ไปยืนยัน Email
            // ----------------------------------------------------

            model.addAttribute(
                    "message",
                    "สมัครสมาชิกสำเร็จ กรุณาตรวจสอบ Email และกดลิงก์เพื่อยืนยันบัญชี"
            );

            return "register-success";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "register";
        }
    }

    // ============================================================
    // EMAIL VERIFIED PAGE
    // ============================================================

    @GetMapping("/auth/verified")
    public String emailVerified(Model model) {

        model.addAttribute(
                "message",
                "ยืนยัน Email สำเร็จแล้ว"
        );

        return "verified";
    }

    // ============================================================
    // DASHBOARD PAGE
    // ============================================================

    @GetMapping("/dashboard")
    public String showDashboard(
            HttpSession session,
            Model model) {

        String accessToken =
                (String) session.getAttribute(
                        "accessToken"
                );

        // ----------------------------------------------------
        // ถ้ายังไม่ได้ Login
        // ----------------------------------------------------

        if (accessToken == null) {

            return "redirect:/login";
        }

        model.addAttribute(
                "message",
                "Login successful!"
        );

        return "dashboard";
    }

    // ============================================================
    // FORGOT PASSWORD PAGE
    // ============================================================

    @GetMapping("/forgot")
    public String forgotPassword() {

        return "forgot";
    }

    // ============================================================
    // FORGOT PASSWORD PROCESS
    // ============================================================

    @PostMapping("/forgot")
    public String processForgotPassword(
            @RequestParam String email,
            Model model) {

        try {

            authService.forgotPassword(email);

            model.addAttribute(
                    "message",
                    "ส่งลิงก์รีเซ็ตรหัสผ่านไปยัง Email ของคุณแล้ว"
            );

            return "forgot";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "forgot";
        }
    }
}