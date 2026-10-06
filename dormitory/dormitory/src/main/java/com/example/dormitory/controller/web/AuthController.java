package com.example.dormitory.controller.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.SpringSecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.repository.AdminRepository;

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

    // =========================================================
    // LOGIN PAGE
    // =========================================================

    @GetMapping("/login")
    public String showLoginPage(Model model) {

        model.addAttribute(
                "loginRequest",
                new LoginRequest()
        );

        return "login";
    }

    // =========================================================
    // LOGIN PROCESS
    // =========================================================

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        try {

            SupabaseAuthResponse authResponse =
                    authService.login(loginRequest);

            UUID userId =
                    UUID.fromString(
                            authResponse.getUser().getId()
                    );

            Authentication authentication =
                    springSecurityService.createAuthentication(userId);

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);

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

            session.setAttribute(
                    "accessToken",
                    authResponse.getAccess_token()
            );

            session.setAttribute(
                    "refreshToken",
                    authResponse.getRefresh_token()
            );

            session.setAttribute(
                    "userId",
                    authResponse.getUser().getId()
            );

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

    // =========================================================
    // REGISTER PAGE
    // =========================================================

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "register";
    }

    // =========================================================
    // REGISTER PROCESS
    // =========================================================

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

            authService.register(registerRequest);

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

    // =========================================================
    // EMAIL VERIFIED PAGE
    // =========================================================

    @GetMapping("/auth/verified")
    public String emailVerified(Model model) {

        model.addAttribute(
                "message",
                "ยืนยัน Email สำเร็จแล้ว"
        );

        return "verified";
    }

    // =========================================================
    // DASHBOARD PAGE
    // =========================================================

    @GetMapping("/dashboard")
    public String showDashboard(
            HttpSession session,
            Model model) {

        String accessToken =
                (String) session.getAttribute(
                        "accessToken"
                );

        if (accessToken == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "message",
                "Login successful!"
        );

        return "dashboard";
    }

    // =========================================================
    // FORGOT PASSWORD PAGE
    // =========================================================

    @GetMapping("/forgot")
    public String forgotPassword() {

        return "forgot";
    }

    // =========================================================
    // FORGOT PASSWORD PROCESS
    // =========================================================

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

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "forgot";
        }
    }

    // =========================================================
    // RESET PASSWORD PAGE
    // =========================================================

    @GetMapping("/reset-password")
    public String resetPasswordPage() {

        return "reset-password";
    }

    // =========================================================
    // RESET PASSWORD API
    // =========================================================

    @PostMapping("/api/v1/auth/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        try {

            if (request == null) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success",
                                false,
                                "message",
                                "Request is required"
                        ));
            }

            if (request.accessToken() == null
                    || request.accessToken().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success",
                                false,
                                "message",
                                "Reset token is required"
                        ));
            }

            if (request.password() == null
                    || request.password().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success",
                                false,
                                "message",
                                "Password is required"
                        ));
            }

            if (request.confirmPassword() == null
                    || request.confirmPassword().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success",
                                false,
                                "message",
                                "Password confirmation is required"
                        ));
            }

            if (!request.password()
                    .equals(request.confirmPassword())) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success",
                                false,
                                "message",
                                "Passwords do not match"
                        ));
            }

            authService.resetPassword(
                    request.accessToken(),
                    request.password()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success",
                            true,
                            "message",
                            "Password reset successfully"
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "success",
                            false,
                            "message",
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Unable to reset password"
                    ));
        }
    }

    // =========================================================
    // RESET PASSWORD REQUEST
    // =========================================================

    public record ResetPasswordRequest(
            String accessToken,
            String password,
            String confirmPassword
    ) {
    }
}