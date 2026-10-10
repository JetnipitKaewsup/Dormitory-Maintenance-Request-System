
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
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.LoginSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AdminLoginControllerTest {

    @Mock
    private AuthService authService;

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
    private LoginController controller;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("test-password");
    }

    private void mockSuccessfulLogin() {
        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(loginSessionService.establishSession(
                authResponse, request, response))
                .thenReturn("/admin/requests");
    }

    /**
     * TC-UT-08-01
     * ตรวจสอบการเข้าสู่ระบบด้วยบัญชี Admin สำเร็จ
     */
    @Test
    void TC_UT_08_01_shouldLoginSuccessfullyAsAdmin() {
        mockSuccessfulLogin();

        String result = controller.processLogin(
                loginRequest, request, response, model);

        assertEquals("redirect:/admin/requests", result);
        verify(authService).login(loginRequest);
    }

    /**
     * TC-UT-08-02
     * ตรวจสอบการส่ง Authentication Response
     * ไปยัง LoginSessionService
     */
    @Test
    void TC_UT_08_02_shouldEstablishSessionAfterAuthentication() {
        mockSuccessfulLogin();

        controller.processLogin(
                loginRequest, request, response, model);

        verify(loginSessionService).establishSession(
                authResponse, request, response);
    }

    /**
     * TC-UT-08-03
     * ตรวจสอบการส่ง Request และ Response ให้ Service
     */
    @Test
    void TC_UT_08_03_shouldPassRequestAndResponseToSessionService() {
        mockSuccessfulLogin();

        controller.processLogin(
                loginRequest, request, response, model);

        verify(loginSessionService).establishSession(
                authResponse, request, response);
    }

    /**
     * TC-UT-08-04
     * ตรวจสอบว่า Controller มอบหมายการจัดการ Session ให้ Service
     */
    @Test
    void TC_UT_08_04_shouldDelegateSessionManagementToService() {
        mockSuccessfulLogin();

        controller.processLogin(
                loginRequest, request, response, model);

        verify(loginSessionService).establishSession(
                authResponse, request, response);
    }

    /**
     * TC-UT-08-05
     * ตรวจสอบการ Redirect Admin ไปหน้าจัดการคำร้อง
     */
    @Test
    void TC_UT_08_05_shouldRedirectAdminToRequestManagementPage() {
        mockSuccessfulLogin();

        String result = controller.processLogin(
                loginRequest, request, response, model);

        assertEquals("redirect:/admin/requests", result);
    }

    /**
     * ตรวจสอบว่าการ Login ล้มเหลวจะกลับไปหน้า Login
     */
    @Test
    void shouldReturnLoginPageWhenAuthenticationFails() {
        when(authService.login(loginRequest))
                .thenThrow(new RuntimeException("Authentication failed"));

        String result = controller.processLogin(
                loginRequest, request, response, model);

        assertEquals("login", result);
        verify(model).addAttribute(
                "error", "Invalid email or password");
    }

    /**
     * ตรวจสอบว่าหากสร้าง Session ไม่สำเร็จ
     * Controller จะไม่ Redirect ไปหน้าปลายทาง
     */
    @Test
    void shouldReturnLoginPageWhenSessionEstablishmentFails() {
        when(authService.login(loginRequest))
                .thenReturn(authResponse);

        when(loginSessionService.establishSession(
                authResponse, request, response))
                .thenThrow(new RuntimeException("Session creation failed"));

        String result = controller.processLogin(
                loginRequest, request, response, model);

        assertEquals("login", result);
        verify(model).addAttribute(
                "error", "Invalid email or password");
    }
}
