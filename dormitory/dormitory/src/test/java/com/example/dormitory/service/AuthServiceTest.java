package com.example.dormitory.service;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;

import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@SuppressWarnings({
        "rawtypes",
        "unchecked"
})
class AuthServiceTest {

    @Mock
    private WebClient supabaseWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    /*
     * ใช้ raw type เพื่อหลีกเลี่ยงปัญหา
     * RequestHeadersSpec<CAP#1> / RequestHeadersSpec<CAP#2>
     * ของ Mockito
     */
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private ObjectMapper objectMapper;

    private AuthService authService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        authService = new AuthService(
                supabaseWebClient,
                objectMapper
        );
    }

    // =========================================================
    // TC-JU-02-01
    // Login ด้วยข้อมูลที่ถูกต้อง
    // =========================================================

    @Test
    void TC_JU_02_01_loginWithValidData_shouldSuccess() {

        LoginRequest request = new LoginRequest();

        request.setEmail(
                "hathaipat012@gmail.com"
        );

        request.setPassword(
                "123456"
        );

        SupabaseAuthResponse expectedResponse =
                new SupabaseAuthResponse();

        expectedResponse.setAccess_token(
                "access-token"
        );

        expectedResponse.setRefresh_token(
                "refresh-token"
        );

        when(supabaseWebClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(anyString()))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(
                MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.bodyValue(any()))
                .thenReturn(requestHeadersSpec);

        /*
         * Mock exchangeToMono()
         *
         * จำลอง Supabase ตอบ HTTP 200
         */
        when(requestHeadersSpec.exchangeToMono(any()))
                .thenAnswer(invocation -> {

                    Function function =
                            invocation.getArgument(0);

                    /*
                     * ไม่เรียก Function จริง เพราะต้องสร้าง
                     * ClientResponse จริงซึ่งไม่จำเป็นสำหรับ
                     * Unit Test validation
                     *
                     * คืน response ที่คาดหวังโดยตรง
                     */
                    return Mono.just(expectedResponse);
                });

        SupabaseAuthResponse result =
                authService.login(request);

        assertNotNull(result);

        assertEquals(
                "access-token",
                result.getAccess_token()
        );

        assertEquals(
                "refresh-token",
                result.getRefresh_token()
        );

        verify(supabaseWebClient)
                .post();
    }

    // =========================================================
    // TC-JU-02-02
    // LoginRequest เป็น null
    // =========================================================

    @Test
    void TC_JU_02_02_loginWithNullRequest_shouldReject() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(null)
                );

        assertEquals(
                "Login request is required",
                exception.getMessage()
        );

        verify(
                supabaseWebClient,
                never()
        ).post();
    }

    // =========================================================
    // TC-JU-02-03
    // ไม่กรอก Email
    // =========================================================

    @Test
    void TC_JU_02_03_loginWithoutEmail_shouldReject() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("");

        request.setPassword(
                "123456"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Email is required",
                exception.getMessage()
        );

        verify(
                supabaseWebClient,
                never()
        ).post();
    }

    // =========================================================
    // TC-JU-02-04
    // Email format ผิด
    // =========================================================

    @Test
    void TC_JU_02_04_loginWithInvalidEmail_shouldReject() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail(
                "hathaipat@"
        );

        request.setPassword(
                "123456"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid email format",
                exception.getMessage()
        );

        verify(
                supabaseWebClient,
                never()
        ).post();
    }

    // =========================================================
    // TC-JU-02-05
    // ไม่กรอก Password
    // =========================================================

    @Test
    void TC_JU_02_05_loginWithoutPassword_shouldReject() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail(
                "hathaipat012@gmail.com"
        );

        request.setPassword("");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Password is required",
                exception.getMessage()
        );

        verify(
                supabaseWebClient,
                never()
        ).post();
    }

    // =========================================================
    // TC-JU-02-06
    // Supabase ส่ง Error
    // =========================================================

    @Test
    void TC_JU_02_06_loginWhenSupabaseReturnsError_shouldReject() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail(
                "hathaipat012@gmail.com"
        );

        request.setPassword(
                "wrong123"
        );

        when(supabaseWebClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(anyString()))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(
                MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.bodyValue(any()))
                .thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.exchangeToMono(any()))
                .thenReturn(
                        Mono.error(
                                new RuntimeException(
                                        "Supabase Auth Error: 401"
                                )
                        )
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Supabase Auth Error: 401",
                exception.getMessage()
        );
    }

    // =========================================================
    // TC-JU-02-07
    // Supabase Response Parse ไม่ได้
    // =========================================================

    @Test
    void TC_JU_02_07_loginWithInvalidSupabaseResponse_shouldReject() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail(
                "hathaipat012@gmail.com"
        );

        request.setPassword(
                "123456"
        );

        when(supabaseWebClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(anyString()))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(
                MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.bodyValue(any()))
                .thenReturn(requestHeadersSpec);

        RuntimeException parseException =
                new RuntimeException(
                        "JSON parse error"
                );

        /*
         * จำลอง exchangeToMono แล้วเกิด
         * Cannot parse Supabase response
         */
        when(requestHeadersSpec.exchangeToMono(any()))
                .thenReturn(
                        Mono.error(
                                new RuntimeException(
                                        "Cannot parse Supabase response",
                                        parseException
                                )
                        )
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Cannot parse Supabase response",
                exception.getMessage()
        );
    }

    // =========================================================
    // TC-JU-02-08
    // Email เป็น whitespace
    // =========================================================

    @Test
    void TC_JU_02_08_loginWithBlankEmail_shouldReject() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail(
                "   "
        );

        request.setPassword(
                "123456"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Email is required",
                exception.getMessage()
        );

        verify(
                supabaseWebClient,
                never()
        ).post();
    }

    // =========================================================
    // TC-JU-02-09
    // Password เป็น whitespace
    // =========================================================

    @Test
    void TC_JU_02_09_loginWithBlankPassword_shouldReject() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail(
                "hathaipat012@gmail.com"
        );

        request.setPassword(
                "   "
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Password is required",
                exception.getMessage()
        );

        verify(
                supabaseWebClient,
                never()
        ).post();
    }
}