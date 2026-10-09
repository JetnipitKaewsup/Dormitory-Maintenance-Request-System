package com.example.dormitory.controller.web;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.SupabaseUser;
import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.SpringSecurityService;

import jakarta.servlet.http.HttpSession;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
class AuthControllerTS08IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private SpringSecurityService springSecurityService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AdminRepository adminRepository;

    private UUID userId;
    private UUID adminId;

    private User user;
    private Admin admin;

    private Authentication authentication;

    private final String email = "admin.integration@test.com";
    private final String password = "admin123";

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        /*
         * User ที่ Controller จะค้นหาจาก UserRepository
         */
        user = new User();

        user.setUserId(userId);
        user.setFirstName("Integration");
        user.setLastName("Admin");
        user.setUsername("integration_admin");
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNo("0812345678");
        user.setRole("ADMIN");

        /*
         * Admin ที่สัมพันธ์กับ User
         */
        admin = new Admin();

        admin.setAdminId(adminId);
        admin.setUser(user);

        /*
         * Authentication ที่สร้างโดย SpringSecurityService
         */
        authentication = org.mockito.Mockito.mock(
                Authentication.class
        );

        org.mockito.Mockito.doReturn(
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_ADMIN"
                        )
                )
        ).when(authentication).getAuthorities();
    }

    /**
     * สร้าง Response จำลองจาก Supabase
     */
    private SupabaseAuthResponse createSuccessfulAuthResponse() {

        SupabaseUser supabaseUser =
                new SupabaseUser();

        supabaseUser.setId(
                userId.toString()
        );

        SupabaseAuthResponse response =
                new SupabaseAuthResponse();

        response.setAccess_token(
                "integration-access-token"
        );

        response.setRefresh_token(
                "integration-refresh-token"
        );

        response.setToken_type("bearer");

        response.setExpires_in(3600);

        response.setUser(
                supabaseUser
        );

        return response;
    }

    /**
     * TC-IT-08-01
     * ตรวจสอบการ Login ผ่าน HTTP Endpoint
     */
    @Test
    void TC_IT_08_01_shouldLoginAdminThroughHttpEndpoint()
            throws Exception {

        when(authService.login(
                any(LoginRequest.class)
        )).thenReturn(
                createSuccessfulAuthResponse()
        );

        when(springSecurityService.createAuthentication(
                userId
        )).thenReturn(authentication);

        when(userRepository.findById(
                userId
        )).thenReturn(
                java.util.Optional.of(user)
        );

        when(adminRepository.findByUser(
                user
        )).thenReturn(
                java.util.Optional.of(admin)
        );

        mockMvc.perform(
                post("/login")
                        .with(csrf())
                        .param("email", email)
                        .param("password", password)
        )
        .andExpect(
                redirectedUrl(
                        "/admin/requests"
                )
        );
    }

    /**
     * TC-IT-08-02
     * ตรวจสอบการเชื่อมต่อระหว่าง Login Controller
     * และ SpringSecurityService
     */
    @Test
    void TC_IT_08_02_shouldCreateAdminAuthentication()
            throws Exception {

        when(authService.login(
                any(LoginRequest.class)
        )).thenReturn(
                createSuccessfulAuthResponse()
        );

        when(springSecurityService.createAuthentication(
                userId
        )).thenReturn(authentication);

        when(userRepository.findById(
                userId
        )).thenReturn(
                java.util.Optional.of(user)
        );

        when(adminRepository.findByUser(
                user
        )).thenReturn(
                java.util.Optional.of(admin)
        );

        mockMvc.perform(
                post("/login")
                        .with(csrf())
                        .param("email", email)
                        .param("password", password)
        )
        .andExpect(
                redirectedUrl(
                        "/admin/requests"
                )
        );

        org.mockito.Mockito.verify(
                springSecurityService
        ).createAuthentication(userId);
    }

    /**
     * TC-IT-08-03
     * ตรวจสอบการค้นหา User และ Admin
     * หลังจาก Login สำเร็จ
     */
    @Test
    void TC_IT_08_03_shouldLoadUserAndAdminInformation()
            throws Exception {

        when(authService.login(
                any(LoginRequest.class)
        )).thenReturn(
                createSuccessfulAuthResponse()
        );

        when(springSecurityService.createAuthentication(
                userId
        )).thenReturn(authentication);

        when(userRepository.findById(
                userId
        )).thenReturn(
                java.util.Optional.of(user)
        );

        when(adminRepository.findByUser(
                user
        )).thenReturn(
                java.util.Optional.of(admin)
        );

        mockMvc.perform(
                post("/login")
                        .with(csrf())
                        .param("email", email)
                        .param("password", password)
        )
        .andExpect(
                redirectedUrl(
                        "/admin/requests"
                )
        );

        org.mockito.Mockito.verify(
                userRepository
        ).findById(userId);

        org.mockito.Mockito.verify(
                adminRepository
        ).findByUser(user);
    }

    /**
     * TC-IT-08-04
     * ตรวจสอบการสร้าง Session
     * หลัง Login สำเร็จ
     */
    @Test
    void TC_IT_08_04_shouldCreateLoginSession()
            throws Exception {

        when(authService.login(
                any(LoginRequest.class)
        )).thenReturn(
                createSuccessfulAuthResponse()
        );

        when(springSecurityService.createAuthentication(
                userId
        )).thenReturn(authentication);

        when(userRepository.findById(
                userId
        )).thenReturn(
                java.util.Optional.of(user)
        );

        when(adminRepository.findByUser(
                user
        )).thenReturn(
                java.util.Optional.of(admin)
        );

        MvcResult result =
                mockMvc.perform(
                        post("/login")
                                .with(csrf())
                                .param("email", email)
                                .param("password", password)
                )
                .andExpect(
                        redirectedUrl(
                                "/admin/requests"
                        )
                )
                .andReturn();

        HttpSession session =
                result.getRequest()
                        .getSession(false);

        assertNotNull(session);

        assertNotNull(
                session.getAttribute(
                        "accessToken"
                )
        );

        assertNotNull(
                session.getAttribute(
                        "refreshToken"
                )
        );

        assertNotNull(
                session.getAttribute(
                        "userId"
                )
        );

        assertNotNull(
                session.getAttribute(
                        "role"
                )
        );
    }

    /**
     * TC-IT-08-05
     * ตรวจสอบการส่ง Admin ไปยังหน้าจัดการคำร้อง
     */
    @Test
    void TC_IT_08_05_shouldRedirectAdminToRequestManagement()
            throws Exception {

        when(authService.login(
                any(LoginRequest.class)
        )).thenReturn(
                createSuccessfulAuthResponse()
        );

        when(springSecurityService.createAuthentication(
                userId
        )).thenReturn(authentication);

        when(userRepository.findById(
                userId
        )).thenReturn(
                java.util.Optional.of(user)
        );

        when(adminRepository.findByUser(
                user
        )).thenReturn(
                java.util.Optional.of(admin)
        );

        mockMvc.perform(
                post("/login")
                        .with(csrf())
                        .param("email", email)
                        .param("password", password)
        )
        .andExpect(
                redirectedUrl(
                        "/admin/requests"
                )
        );
    }
}