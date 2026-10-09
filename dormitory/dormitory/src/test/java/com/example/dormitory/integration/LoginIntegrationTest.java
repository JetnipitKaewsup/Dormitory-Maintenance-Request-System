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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.controller.web.AuthController;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.repository.AdminRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.AuthService;
import com.example.dormitory.service.LoginSessionService;
import com.example.dormitory.service.SpringSecurityService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.ObjectMapper;

class LoginIntegrationTest {

    private HttpServer mockSupabaseServer;

    private AuthService authService;
    private AuthController authController;
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

        /*
         * ---------------------------------------------------------
         * 1. Start Mock Supabase Server
         * ---------------------------------------------------------
         */

        mockSupabaseServer = HttpServer.create(
                new InetSocketAddress(0),
                0
        );

        mockSupabaseServer.createContext(
                "/auth/v1/token",
                this::handleSupabaseLogin
        );

        mockSupabaseServer.start();

        int port = mockSupabaseServer.getAddress().getPort();

        /*
         * ---------------------------------------------------------
         * 2. Create real WebClient
         * ---------------------------------------------------------
         */

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        /*
         * ---------------------------------------------------------
         * 3. Create REAL AuthService
         * ---------------------------------------------------------
         */

        authService = new AuthService(
                webClient,
                objectMapper
        );

        /*
         * ---------------------------------------------------------
         * 4. Create mocked dependencies
         * ---------------------------------------------------------
         */

        springSecurityService = mock(SpringSecurityService.class);
        userRepository = mock(UserRepository.class);
        adminRepository = mock(AdminRepository.class);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        user = mock(User.class);

        /*
         * จำลอง Session ที่ยังไม่มีอยู่ก่อน Login
         * จากนั้น Spring Security จะสร้าง Session ใหม่
         */
        when(request.getSession(false))
                .thenReturn(null, session);

        when(request.getSession(true))
                .thenReturn(session);

        when(request.getSession())
                .thenReturn(session);

        /*
         * ---------------------------------------------------------
         * 5. Create LoginSessionService
         * ---------------------------------------------------------
         */

        loginSessionService = new LoginSessionService(
                springSecurityService,
                userRepository,
                adminRepository
        );

        /*
         * ---------------------------------------------------------
         * 6. Create REAL AuthController
         * ---------------------------------------------------------
         */

        authController = new AuthController(
                authService,
                springSecurityService,
                userRepository,
                adminRepository,
                loginSessionService
        );

        userId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {

        if (mockSupabaseServer != null) {
            mockSupabaseServer.stop(0);
        }

        SecurityContextHolder.clearContext();
    }

    /*
     * =============================================================
     * Mock Supabase
     * =============================================================
     */

    private void handleSupabaseLogin(
            HttpExchange exchange) throws IOException {

        if (!"POST".equalsIgnoreCase(
                exchange.getRequestMethod())) {

            writeResponse(
                    exchange,
                    405,
                    """
                    {
                        "error": "Method Not Allowed"
                    }
                    """
            );

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

    private void writeResponse(
            HttpExchange exchange,
            int status,
            String body) throws IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    /*
     * =============================================================
     * Test Helpers
     * =============================================================
     */

    private com.example.dormitory.dto.request.LoginRequest
            createValidLoginRequest() {

        var loginRequest =
                new com.example.dormitory.dto.request.LoginRequest();

        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("test-password");

        return loginRequest;
    }

    private Authentication configureReporterAuthentication() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userId.toString(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_REPORTER"
                                )
                        )
                );

        when(springSecurityService.createAuthentication(userId))
                .thenReturn(authentication);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(user.getFirstName())
                .thenReturn("Test");

        when(user.getLastName())
                .thenReturn("Reporter");

        return authentication;
    }

    /*
     * =============================================================
     * TC-IT-02-01
     *
     * Login ด้วยข้อมูลที่ถูกต้อง
     * =============================================================
     */

    @Test
    void TC_IT_02_01_loginWithValidData_shouldSuccess()
            throws Exception {

        var loginRequest = createValidLoginRequest();

        configureReporterAuthentication();

        Model model = new ExtendedModelMap();

        String result = authController.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals(
                "redirect:/reporter/requests",
                result
        );

        verify(springSecurityService)
                .createAuthentication(userId);

        verify(session).setAttribute(
                "accessToken",
                "test-access-token"
        );

        verify(session).setAttribute(
                "refreshToken",
                "test-refresh-token"
        );

        verify(session).setAttribute(
                "userId",
                userId.toString()
        );

        verify(session).setAttribute(
                "userFullName",
                "Test Reporter"
        );

        verify(session).setAttribute(
                "role",
                "REPORTER"
        );
    }

    /*
     * =============================================================
     * TC-IT-02-02
     *
     * Email / Password ไม่ถูกต้อง
     * =============================================================
     */

    @Test
    void TC_IT_02_02_loginWithWrongPassword_shouldReturnLoginPage()
            throws Exception {

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
                        """
                )
        );

        var loginRequest = createValidLoginRequest();
        loginRequest.setPassword("wrong-password");

        Model model = new ExtendedModelMap();

        String result = authController.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals("login", result);

        assertEquals(
                "Invalid email or password",
                model.getAttribute("error")
        );

        verify(
                springSecurityService,
                never()
        ).createAuthentication(any());

        verify(
                session,
                never()
        ).setAttribute(
                eq("accessToken"),
                any()
        );
    }

    /*
     * =============================================================
     * TC-IT-02-03
     *
     * ไม่กรอก Password
     * =============================================================
     */

    @Test
    void TC_IT_02_03_loginWithoutPassword_shouldReturnLoginPage() {

        var loginRequest = createValidLoginRequest();
        loginRequest.setPassword("");

        Model model = new ExtendedModelMap();

        String result = authController.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals("login", result);

        assertEquals(
                "Invalid email or password",
                model.getAttribute("error")
        );

        verify(
                springSecurityService,
                never()
        ).createAuthentication(any());

        verify(
                session,
                never()
        ).setAttribute(
                eq("accessToken"),
                any()
        );
    }

    /*
     * =============================================================
     * TC-IT-02-04
     *
     * ไม่กรอก Email
     * =============================================================
     */

    @Test
    void TC_IT_02_04_loginWithoutEmail_shouldReturnLoginPage() {

        var loginRequest = createValidLoginRequest();
        loginRequest.setEmail("");

        Model model = new ExtendedModelMap();

        String result = authController.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals("login", result);

        assertEquals(
                "Invalid email or password",
                model.getAttribute("error")
        );

        verify(
                springSecurityService,
                never()
        ).createAuthentication(any());

        verify(
                session,
                never()
        ).setAttribute(
                eq("accessToken"),
                any()
        );
    }

    /*
     * =============================================================
     * TC-IT-02-05
     *
     * ตรวจสอบการสร้าง Authentication
     * =============================================================
     */

    @Test
    void TC_IT_02_05_loginSuccessfully_shouldCreateAuthentication()
            throws Exception {

        var loginRequest = createValidLoginRequest();

        Authentication authentication =
                configureReporterAuthentication();

        Model model = new ExtendedModelMap();

        String result = authController.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        assertEquals(
                "redirect:/reporter/requests",
                result
        );

        verify(springSecurityService, times(1))
                .createAuthentication(userId);

        assertNotNull(authentication);

        assertTrue(authentication.isAuthenticated());

        assertEquals(
                "ROLE_REPORTER",
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );
    }

    /*
     * =============================================================
     * TC-IT-02-06
     *
     * ตรวจสอบการจัดเก็บข้อมูล Login ลง Session
     * =============================================================
     */

    @Test
    void TC_IT_02_06_loginSuccessfully_shouldStoreLoginDataInSession()
            throws Exception {

        var loginRequest = createValidLoginRequest();

        configureReporterAuthentication();

        Model model = new ExtendedModelMap();

        authController.processLogin(
                loginRequest,
                request,
                response,
                model
        );

        verify(session).setAttribute(
                "accessToken",
                "test-access-token"
        );

        verify(session).setAttribute(
                "refreshToken",
                "test-refresh-token"
        );

        verify(session).setAttribute(
                "userId",
                userId.toString()
        );

        verify(session).setAttribute(
                "userFullName",
                "Test Reporter"
        );

        verify(session).setAttribute(
                "role",
                "REPORTER"
        );
    }
}
