
package com.example.dormitory.controller.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.LoginSessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(LoginController.class)
class AuthControllerTS08IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private LoginSessionService loginSessionService;

    @Test
    @DisplayName("TS-08 - Login successfully as Admin")
    void loginAsAdmin_shouldRedirectToAdminRequests()
            throws Exception {

        SupabaseAuthResponse authResponse =
                org.mockito.Mockito.mock(SupabaseAuthResponse.class);

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        when(loginSessionService.establishSession(
                any(SupabaseAuthResponse.class),
                any(HttpServletRequest.class),
                any(HttpServletResponse.class)))
                .thenReturn("/admin/requests");

        mockMvc.perform(post("/login")
                .with(csrf())
                .param("email", "admin@example.com")
                .param("password", "test-password"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/admin/requests"));

        verify(authService).login(any(LoginRequest.class));
        verify(loginSessionService).establishSession(
                any(SupabaseAuthResponse.class),
                any(HttpServletRequest.class),
                any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("TS-08 - Login fails")
    void loginFailure_shouldReturnLoginPage()
            throws Exception {

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Login failed"));

        mockMvc.perform(post("/login")
                .with(csrf())
                .param("email", "admin@example.com")
                .param("password", "wrong-password"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute(
                    "error", "Invalid email or password"));

        verify(authService).login(any(LoginRequest.class));

        verify(loginSessionService, never()).establishSession(
                any(SupabaseAuthResponse.class),
                any(HttpServletRequest.class),
                any(HttpServletResponse.class));
    }
}
