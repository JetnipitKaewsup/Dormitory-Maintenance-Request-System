package com.example.dormitory.controller.web;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.SupabaseUser;
import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.SpringSecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class AdminLoginControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private SpringSecurityService springSecurityService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private SupabaseAuthResponse authResponse;

    @Mock
    private SupabaseUser supabaseUser;

    @Mock
    private User user;

    @Mock
    private Admin admin;

    @Mock
    private Authentication authentication;

    private AuthController controller;

    private UUID userId;
    private UUID adminId;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        controller = new AuthController(
                authService,
                springSecurityService,
                userRepository,
                adminRepository
        );

        userId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        when(request.getSession())
                .thenReturn(session);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * TC-UT-08-01
     * ตรวจสอบการเข้าสู่ระบบด้วยบัญชี Admin สำเร็จ
     */
    @Test
    void TC_UT_08_01_shouldLoginSuccessfullyAsAdmin() {

        LoginRequest loginRequest = new LoginRequest();

        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(authResponse.getUser())
                .thenReturn(supabaseUser);

        when(supabaseUser.getId())
                .thenReturn(userId.toString());

        when(authResponse.getAccess_token())
                .thenReturn("access-token");

        when(authResponse.getRefresh_token())
                .thenReturn("refresh-token");

        when(springSecurityService.createAuthentication(userId))
                .thenReturn(authentication);

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        ).when(authentication).getAuthorities();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getFirstName())
                .thenReturn("Admin");

        when(user.getLastName())
                .thenReturn("User");

        when(adminRepository.findByUser(user))
                .thenReturn(Optional.of(admin));

        when(admin.getAdminId())
                .thenReturn(adminId);

        String result = controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals(
                "redirect:/admin/requests",
                result
        );

        verify(authService)
                .login(loginRequest);
    }

    /**
     * TC-UT-08-02
     * ตรวจสอบการสร้าง Authentication ด้วย User ID
     */
    @Test
    void TC_UT_08_02_shouldCreateAuthenticationForAdminUser() {

        LoginRequest loginRequest = new LoginRequest();

        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(authResponse.getUser())
                .thenReturn(supabaseUser);

        when(supabaseUser.getId())
                .thenReturn(userId.toString());

        when(authResponse.getAccess_token())
                .thenReturn("access-token");

        when(authResponse.getRefresh_token())
                .thenReturn("refresh-token");

        when(springSecurityService.createAuthentication(userId))
                .thenReturn(authentication);

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        ).when(authentication).getAuthorities();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getFirstName())
                .thenReturn("Admin");

        when(user.getLastName())
                .thenReturn("User");

        when(adminRepository.findByUser(user))
                .thenReturn(Optional.of(admin));

        when(admin.getAdminId())
                .thenReturn(adminId);

        controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        verify(springSecurityService)
                .createAuthentication(userId);
    }

    /**
     * TC-UT-08-03
     * ตรวจสอบการบันทึกข้อมูล Login ลง Session
     */
    @Test
    void TC_UT_08_03_shouldStoreAdminLoginInformationInSession() {

        LoginRequest loginRequest = new LoginRequest();

        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(authResponse.getUser())
                .thenReturn(supabaseUser);

        when(supabaseUser.getId())
                .thenReturn(userId.toString());

        when(authResponse.getAccess_token())
                .thenReturn("access-token");

        when(authResponse.getRefresh_token())
                .thenReturn("refresh-token");

        when(springSecurityService.createAuthentication(userId))
                .thenReturn(authentication);

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        ).when(authentication).getAuthorities();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getFirstName())
                .thenReturn("Admin");

        when(user.getLastName())
                .thenReturn("User");

        when(adminRepository.findByUser(user))
                .thenReturn(Optional.of(admin));

        when(admin.getAdminId())
                .thenReturn(adminId);

        controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        verify(session)
                .setAttribute(
                        "accessToken",
                        "access-token"
                );

        verify(session)
                .setAttribute(
                        "refreshToken",
                        "refresh-token"
                );

        verify(session)
                .setAttribute(
                        "userId",
                        userId.toString()
                );

        verify(session)
                .setAttribute(
                        "userFullName",
                        "Admin User"
                );

        verify(session)
                .setAttribute(
                        "role",
                        "ADMIN"
                );
    }

    /**
     * TC-UT-08-04
     * ตรวจสอบการบันทึก Admin ID ลง Session
     */
    @Test
    void TC_UT_08_04_shouldStoreAdminIdInSession() {

        LoginRequest loginRequest = new LoginRequest();

        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(authResponse.getUser())
                .thenReturn(supabaseUser);

        when(supabaseUser.getId())
                .thenReturn(userId.toString());

        when(authResponse.getAccess_token())
                .thenReturn("access-token");

        when(authResponse.getRefresh_token())
                .thenReturn("refresh-token");

        when(springSecurityService.createAuthentication(userId))
                .thenReturn(authentication);

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        ).when(authentication).getAuthorities();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getFirstName())
                .thenReturn("Admin");

        when(user.getLastName())
                .thenReturn("User");

        when(adminRepository.findByUser(user))
                .thenReturn(Optional.of(admin));

        when(admin.getAdminId())
                .thenReturn(adminId);

        controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        verify(adminRepository)
                .findByUser(user);

        verify(session)
                .setAttribute(
                        "adminId",
                        adminId
                );
    }

    /**
     * TC-UT-08-05
     * ตรวจสอบการ Redirect ของ Admin
     */
    @Test
    void TC_UT_08_05_shouldRedirectAdminToRequestManagementPage() {

        LoginRequest loginRequest = new LoginRequest();

        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(authResponse.getUser())
                .thenReturn(supabaseUser);

        when(supabaseUser.getId())
                .thenReturn(userId.toString());

        when(authResponse.getAccess_token())
                .thenReturn("access-token");

        when(authResponse.getRefresh_token())
                .thenReturn("refresh-token");

        when(springSecurityService.createAuthentication(userId))
                .thenReturn(authentication);

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        ).when(authentication).getAuthorities();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getFirstName())
                .thenReturn("Admin");

        when(user.getLastName())
                .thenReturn("User");

        when(adminRepository.findByUser(user))
                .thenReturn(Optional.of(admin));

        when(admin.getAdminId())
                .thenReturn(adminId);

        String result = controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals(
                "redirect:/admin/requests",
                result
        );
    }
}
