package com.example.dormitory.service;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class LoginSessionService {

    private final SpringSecurityService springSecurityService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public LoginSessionService(
            SpringSecurityService springSecurityService,
            UserRepository userRepository,
            AdminRepository adminRepository) {

        this.springSecurityService = springSecurityService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    public String establishSession(
            SupabaseAuthResponse authResponse,
            HttpServletRequest request,
            HttpServletResponse response) {

        UUID userId = UUID.fromString(
                authResponse.getUser().getId());

        Authentication authentication =
                springSecurityService.createAuthentication(userId);

        // สร้าง SecurityContext สำหรับผู้ใช้ที่ผ่านการยืนยันตัวตน
        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        // ป้องกัน Session Fixation ด้วยการเปลี่ยน Session หลัง Login
        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = request.getSession(true);

        SecurityContextHolder.setContext(context);

        HttpSessionSecurityContextRepository repository =
                new HttpSessionSecurityContextRepository();

        repository.saveContext(context, request, response);

        // เก็บ Token และข้อมูล Session ที่ระบบเดิมใช้งาน
        session.setAttribute(
                "accessToken",
                authResponse.getAccess_token());

        session.setAttribute(
                "refreshToken",
                authResponse.getRefresh_token());

        session.setAttribute("userId", userId.toString());

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user record was not found"));

        session.setAttribute(
                "userFullName",
                user.getFirstName() + " " + user.getLastName());

        String role = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority ->
                        authority.equals("ROLE_ADMIN")
                        || authority.equals("ROLE_TECHNICIAN")
                        || authority.equals("ROLE_REPORTER"))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "User does not have a supported role"));

        session.setAttribute(
                "role",
                role.substring("ROLE_".length()));

        if ("ROLE_ADMIN".equals(role)) {
            Admin admin = adminRepository.findByUser(user)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Admin profile was not found"));

            session.setAttribute(
                    "adminId",
                    admin.getAdminId());

            return "/admin/requests";
        }

        if ("ROLE_TECHNICIAN".equals(role)) {
            return "/technician/dailywork";
        }

        return "/reporter/requests";
    }
}