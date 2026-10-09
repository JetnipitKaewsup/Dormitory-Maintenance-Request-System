package com.example.dormitory.controller.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.LoginSessionService;
import com.example.dormitory.service.SpringSecurityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
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
    private LoginSessionService loginSessionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Model model;

    @Mock
    private SupabaseAuthResponse authResponse;

    @InjectMocks
    private AuthController controller;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
    }

    private void mockSuccessfulLogin() {
        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(loginSessionService.establishSession(
                authResponse,
                request,
                response
        )).thenReturn("/admin/requests");
    }

    /**
     * TC-UT-08-01
     * ตรวจสอบการเข้าสู่ระบบด้วยบัญชี Admin สำเร็จ
     */
    @Test
    void TC_UT_08_01_shouldLoginSuccessfullyAsAdmin() {

        mockSuccessfulLogin();

        String result = controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals("redirect:/admin/requests", result);

        verify(authService).login(loginRequest);
    }

    /**
     * TC-UT-08-02
     * ตรวจสอบการส่งผลการ Authentication
     * ไปยัง LoginSessionService
     */
    @Test
    void TC_UT_08_02_shouldEstablishSessionAfterAuthentication() {

        mockSuccessfulLogin();

        controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        verify(loginSessionService).establishSession(
                authResponse,
                request,
                response
        );
    }

    /**
     * TC-UT-08-03
     * ตรวจสอบการส่ง HttpServletRequest
     * และ HttpServletResponse สำหรับจัดการ Session
     */
    @Test
    void TC_UT_08_03_shouldPassRequestAndResponseToSessionService() {

        mockSuccessfulLogin();

        controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        verify(loginSessionService).establishSession(
                authResponse,
                request,
                response
        );
    }

    /**
     * TC-UT-08-04
     * ตรวจสอบว่ามีการเรียก Service
     * เพื่อจัดการข้อมูล Session หลัง Login
     */
    @Test
    void TC_UT_08_04_shouldDelegateSessionManagementToService() {

        mockSuccessfulLogin();

        controller.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        verify(loginSessionService).establishSession(
                authResponse,
                request,
                response
        );
    }

    /**
     * TC-UT-08-05
     * ตรวจสอบการ Redirect ของ Admin
     * ไปยังหน้าจัดการคำร้อง
     */
    @Test
    void TC_UT_08_05_shouldRedirectAdminToRequestManagementPage() {

        mockSuccessfulLogin();

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
