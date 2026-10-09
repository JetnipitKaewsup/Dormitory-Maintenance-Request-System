package com.example.dormitory.controller.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.SpringSecurityService;

@WebMvcTest(AuthController.class)
class AuthControllerRegisterIntegrationTest {

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

    // =========================================================
    // TC-IT-01-01
    // =========================================================

    @Test
    @DisplayName(
            "TC-IT-01-01 - Register with valid data"
    )
    void processRegister_validData_shouldReturnSuccessPage()
            throws Exception {

        mockMvc.perform(
                post("/register")
                        .with(csrf())
                        .param("firstName", "Hathaipat")
                        .param("lastName", "Wisutthitam")
                        .param("username", "hathaipat")
                        .param("email", "hathaipat012@gmail.com")
                        .param("roomAddress", "A")
                        .param("roomNumber", "101")
                        .param("password", "123456")
                        .param("confirmPassword", "123456")
                        .param("phoneNo", "0812345678")
                        .param("terms", "true")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("register-success"))
        .andExpect(
                model().attribute(
                        "message",
                        "สมัครสมาชิกสำเร็จ กรุณาตรวจสอบ Email และกดลิงก์เพื่อยืนยันบัญชี"
                )
        );

        verify(
                authService,
                times(1)
        ).register(any(RegisterRequest.class));
    }

    // =========================================================
    // TC-IT-01-02
    // =========================================================

    @Test
    @DisplayName(
            "TC-IT-01-02 - Register when AuthService returns error"
    )
    void processRegister_serviceError_shouldReturnRegisterPage()
            throws Exception {

        doThrow(
                new RuntimeException(
                        "Supabase Auth Error: 400 BAD_REQUEST - Email already registered"
                )
        )
        .when(authService)
        .register(any(RegisterRequest.class));

        mockMvc.perform(
                post("/register")
                        .with(csrf())
                        .param("firstName", "Hathaipat")
                        .param("lastName", "Wisutthitam")
                        .param("username", "hathaipat")
                        .param("email", "hathaipat012@gmail.com")
                        .param("roomAddress", "A")
                        .param("roomNumber", "101")
                        .param("password", "123456")
                        .param("confirmPassword", "123456")
                        .param("phoneNo", "0812345678")
                        .param("terms", "true")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(
                model().attribute(
                        "error",
                        "Supabase Auth Error: 400 BAD_REQUEST - Email already registered"
                )
        );

        verify(
                authService,
                times(1)
        ).register(any(RegisterRequest.class));
    }

    // =========================================================
    // TC-IT-01-03
    // =========================================================

    @Test
    @DisplayName(
            "TC-IT-01-03 - Register with invalid First Name"
    )
    void processRegister_invalidFirstName_shouldReturnRegisterPage()
            throws Exception {

        doThrow(
                new IllegalArgumentException(
                        "First Name is required"
                )
        )
        .when(authService)
        .register(any(RegisterRequest.class));

        mockMvc.perform(
                post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "Wisutthitam")
                        .param("username", "hathaipat")
                        .param("email", "hathaipat012@gmail.com")
                        .param("roomAddress", "A")
                        .param("roomNumber", "101")
                        .param("password", "123456")
                        .param("confirmPassword", "123456")
                        .param("phoneNo", "0812345678")
                        .param("terms", "true")
        )
        .andExpect(status().isOk())
        .andExpect(view().name("register"))
        .andExpect(
                model().attribute(
                        "error",
                        "First Name is required"
                )
        );

        verify(
                authService,
                times(1)
        ).register(any(RegisterRequest.class));
    }
}