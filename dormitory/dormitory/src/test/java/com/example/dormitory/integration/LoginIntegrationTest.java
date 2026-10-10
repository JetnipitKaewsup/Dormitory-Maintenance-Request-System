package com.example.dormitory.integration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.domain.entity.User;
import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.AuthRequestValidator;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.LoginSessionService;
import com.example.dormitory.service.SpringSecurityService;
import com.example.dormitory.service.SupabaseAuthGateway;
import com.example.dormitory.service.impl.SupabaseAuthGatewayImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.ObjectMapper;

class LoginIntegrationTest {

    private HttpServer mockSupabaseServer;

    private AuthService authService;
    private LoginSessionService loginSessionService;

    private SpringSecurityService springSecurityService;
    private UserRepository userRepository;
    private AdminRepository adminRepository;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() throws Exception {
        userId = UUID.randomUUID();

        // เริ่ม Mock HTTP Server สำหรับจำลอง Supabase Auth
        mockSupabaseServer = HttpServer.create(
                new InetSocketAddress(0), 0);

        mockSupabaseServer.createContext(
                "/auth/v1/token",
                this::handleSupabaseLogin);

        mockSupabaseServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(
                        "http://localhost:"
                                + mockSupabaseServer.getAddress().getPort())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        SupabaseAuthGateway gateway =
                new SupabaseAuthGatewayImpl(
                        webClient,
                        objectMapper);

        authService = new AuthService(
                gateway,
                new AuthRequestValidator());

        // Mock dependencies สำหรับสร้าง Security Context และ Session
        springSecurityService = mock(SpringSecurityService.class);
        userRepository = mock(UserRepository.class);
        adminRepository = mock(AdminRepository.class);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        user = mock(User.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);

        loginSessionService = new LoginSessionService(
                springSecurityService,
                userRepository,
                adminRepository);
    }

    @AfterEach
    void tearDown() {
        if (mockSupabaseServer != null) {
            mockSupabaseServer.stop(0);
        }

        SecurityContextHolder.clearContext();
    }

    /**
     * จำลองการตอบกลับสำเร็จจาก Supabase Auth
     */
    private void handleSupabaseLogin(HttpExchange exchange)
            throws IOException {

        if (!"POST".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            writeResponse(
                    exchange,
                    405,
                    "{\"error\":\"Method Not Allowed\"}");

            return;
        }

        String responseBody = """
                {
                    "access_token": "test-access-token",
                    "refresh_token": "test-refresh-token",
                    "token_type": "bearer",
                    "expires_in": 3600,
                    "user": {
                        "id": "%s"
                    }
                }
                """.formatted(userId);

        writeResponse(exchange, 200, responseBody);
    }

    /**
     * ส่ง HTTP Response จาก Mock Supabase Server
     */
    private void writeResponse(
            HttpExchange exchange,
            int status,
            String body) throws IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    /**
     * สร้างข้อมูล Login ที่ถูกต้อง
     */
    private LoginRequest createValidLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("test-password");

        return loginRequest;
    }

    /**
     * เตรียม Authentication สำหรับผู้ใช้ Reporter
     */
    private Authentication configureReporterAuthentication() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_REPORTER")));

        when(springSecurityService.createAuthentication(userId))
                .thenReturn(authentication);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getFirstName()).thenReturn("Test");
        when(user.getLastName()).thenReturn("Reporter");

        return authentication;
    }

    /**
     * TC-IT-02-01:
     * เข้าสู่ระบบด้วยข้อมูลที่ถูกต้องและรับ Token จาก Supabase
     */
    @Test
    void TC_IT_02_01_loginWithValidData_shouldAuthenticateWithSupabase()
            throws Exception {

        SupabaseAuthResponse authResponse =
                authService.login(createValidLoginRequest());

        assertNotNull(authResponse);

        assertEquals(
                "test-access-token",
                authResponse.getAccess_token());

        assertEquals(
                "test-refresh-token",
                authResponse.getRefresh_token());

        assertEquals(
                userId.toString(),
                authResponse.getUser().getId());
    }

    /**
     * TC-IT-02-02:
     * เข้าสู่ระบบด้วย Password ที่ไม่ถูกต้อง
     */
    @Test
    void TC_IT_02_02_loginWithWrongPassword_shouldFail() {

        // เปลี่ยน Mock Supabase ให้ตอบกลับ Unauthorized
        mockSupabaseServer.removeContext("/auth/v1/token");

        mockSupabaseServer.createContext(
                "/auth/v1/token",
                exchange -> writeResponse(
                        exchange,
                        401,
                        """
                        {
                            "error": "invalid_grant",
                            "error_description":
                                "Invalid login credentials"
                        }
                        """));

        LoginRequest loginRequest = createValidLoginRequest();
        loginRequest.setPassword("wrong-password");

        assertThrows(
                Exception.class,
                () -> authService.login(loginRequest));
    }

    /**
     * TC-IT-02-03:
     * เข้าสู่ระบบโดยไม่ระบุ Password
     */
    @Test
    void TC_IT_02_03_loginWithoutPassword_shouldFail() {

        LoginRequest loginRequest = createValidLoginRequest();
        loginRequest.setPassword("");

        assertThrows(
                Exception.class,
                () -> authService.login(loginRequest));
    }

    /**
     * TC-IT-02-04:
     * เข้าสู่ระบบโดยไม่ระบุ Email
     */
    @Test
    void TC_IT_02_04_loginWithoutEmail_shouldFail() {

        LoginRequest loginRequest = createValidLoginRequest();
        loginRequest.setEmail("");

        assertThrows(
                Exception.class,
                () -> authService.login(loginRequest));
    }

    /**
     * TC-IT-02-05:
     * ตรวจสอบว่าระบบสร้าง Authentication หลัง Login สำเร็จ
     */
    @Test
    void TC_IT_02_05_loginSuccessfully_shouldCreateAuthentication()
            throws Exception {

        // Arrange
        configureReporterAuthentication();

        SupabaseAuthResponse authResponse =
                authService.login(createValidLoginRequest());

        // Act
        String destination = loginSessionService.establishSession(
                authResponse,
                request,
                response);

        // Assert
        assertEquals("/reporter/requests", destination);

        verify(springSecurityService)
                .createAuthentication(userId);
    }

    /**
     * TC-IT-02-06:
     * ตรวจสอบการบันทึกข้อมูล Login ลง Session
     */
    @Test
    void TC_IT_02_06_loginSuccessfully_shouldStoreLoginDataInSession()
            throws Exception {

        // Arrange
        configureReporterAuthentication();

        SupabaseAuthResponse authResponse =
                authService.login(createValidLoginRequest());

        // Act
        String destination = loginSessionService.establishSession(
                authResponse,
                request,
                response);

        // Assert
        assertEquals("/reporter/requests", destination);

        verify(session).setAttribute(
                "accessToken",
                "test-access-token");

        verify(session).setAttribute(
                "refreshToken",
                "test-refresh-token");

        verify(session).setAttribute(
                "userId",
                userId.toString());

        verify(session).setAttribute(
                "userFullName",
                "Test Reporter");

        verify(session).setAttribute(
                "role",
                "REPORTER");
    }
}
