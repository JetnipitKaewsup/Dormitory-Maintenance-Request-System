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

import java.util.UUID;


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
    private final UserRepository userRepository;
    private final AdminRepository adminRepository; 

public AuthController(
        AuthService authService,
        SpringSecurityService springSecurityService,
    UserRepository userRepository, 
    AdminRepository adminRepository 
) {

    this.authService = authService;
    this.springSecurityService = springSecurityService;
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;}

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
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        try {
            // ตรวจสอบ email / password กับ supabase
            SupabaseAuthResponse authResponse =
                    authService.login(loginRequest);
            
            UUID userId = UUID.fromString(authResponse.getUser().getId());
            
            // สร้าง Spring Security Authentication
            Authentication authentication = springSecurityService.createAuthentication(userId);
            
            // สร้าง SecurityContext
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            
            // บันทึก SecurityContext ลง Session
            HttpSession session = request.getSession();
            HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
            securityContextRepository.saveContext(context, request, response);

            // เก็บ Supabase token
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
            // ---- เพิ่มใหม่: set ชื่อ/role/adminId ลง session ----
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                session.setAttribute(
                        "userFullName",
                        user.getFirstName() + " " + user.getLastName()
                );
            }
        
            // redirect ตาม Role
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            session.setAttribute("role", role.replace("ROLE_", ""));

            if (role.equals("ROLE_ADMIN")) {

                if (user != null) {
                    adminRepository.findByUser(user)
                            .ifPresent(admin ->
                                    session.setAttribute("adminId", admin.getAdminId())
                            );
                }

                return "redirect:/admin/requests";
            }

            if(role.equals("ROLE_TECHNICIAN")){
                return "redirect:/technician/dailywork";
            }
            
            if(role.equals("ROLE_REPORTER")){
                return "redirect:/reporter/requests";
            }
            
            return "redirect:/reporter/requests";


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