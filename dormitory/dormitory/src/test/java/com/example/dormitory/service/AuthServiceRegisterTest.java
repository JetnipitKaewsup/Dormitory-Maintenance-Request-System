package com.example.dormitory.service;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.dto.request.RegisterRequest;

import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegisterTest {

    @Mock
    private WebClient supabaseWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ClientResponse clientResponse;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthService authService;


    // ============================================================
    // TC-JU-01-01
    // สมัครสมาชิกด้วยข้อมูลที่ถูกต้อง
    // ============================================================

    @Test
    void register_validData_shouldSuccess() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setLastName("Wisutthitam");
        request.setUsername("hathaipat");
        request.setEmail("hathaipat012@gmail.com");
        request.setRoomNumber("A101");
        request.setPhoneNo("0812345678");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setTerms(true);


        when(supabaseWebClient.post())
                .thenReturn(requestBodyUriSpec);

        /*
         * AuthService ใช้ uri builder:
         *
         * .uri(uriBuilder ->
         *      uriBuilder
         *          .path("/auth/v1/signup")
         *          .queryParam("redirect_to", ...)
         *          .build()
         * )
         */
        when(requestBodyUriSpec.uri(
                any(Function.class)
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(
                MediaType.APPLICATION_JSON
        )).thenReturn(requestBodySpec);

        doReturn(requestHeadersSpec)
                .when(requestBodySpec)
                .bodyValue(any(Map.class));

        /*
         * กรณี Success:
         * ให้ exchangeToMono รับ function แล้วจำลอง
         * response ที่ไม่ใช่ error
         */
        when(requestHeadersSpec.exchangeToMono(
                any()
        )).thenAnswer(invocation -> {

            Function<ClientResponse, Mono<String>> function =
                    invocation.getArgument(0);

            when(clientResponse.statusCode())
                    .thenReturn(HttpStatus.OK);

            when(clientResponse.bodyToMono(String.class))
                    .thenReturn(Mono.just(""));

            return function.apply(clientResponse);
        });


        // Act
        authService.register(request);


        // Assert
        verify(supabaseWebClient)
                .post();

        verify(requestBodyUriSpec)
                .uri(any(Function.class));

        verify(requestBodySpec)
                .contentType(MediaType.APPLICATION_JSON);


        // ตรวจสอบ Request Body
        ArgumentCaptor<Map> bodyCaptor =
                ArgumentCaptor.forClass(Map.class);

        verify(requestBodySpec)
                .bodyValue(bodyCaptor.capture());


        Map<String, Object> body =
                bodyCaptor.getValue();


        assertEquals(
                "hathaipat012@gmail.com",
                body.get("email")
        );

        assertEquals(
                "123456",
                body.get("password")
        );


        // ตรวจสอบ metadata
        Map<String, Object> metadata =
                (Map<String, Object>) body.get("data");


        assertEquals(
                "hathaipat012@gmail.com",
                metadata.get("email")
        );

        assertEquals(
                "Hathaipat",
                metadata.get("first_name")
        );

        assertEquals(
                "Wisutthitam",
                metadata.get("last_name")
        );

        assertEquals(
                "hathaipat",
                metadata.get("username")
        );

        assertEquals(
                "A101",
                metadata.get("room_number")
        );

        assertEquals(
                "0812345678",
                metadata.get("phone_no")
        );
    }


    // ============================================================
    // TC-JU-01-02
    // ไม่กรอก First Name
    // ============================================================

    @Test
    void register_emptyFirstName_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("");
        request.setEmail("hathaipat012@gmail.com");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setTerms(true);


        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );


        assertEquals(
                "First Name is required",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-03
    // ไม่กรอก Email
    // ============================================================

    @Test
    void register_emptyEmail_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setEmail("");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setTerms(true);


        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );


        assertEquals(
                "Email is required",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-04
    // Email ผิดรูปแบบ
    // ============================================================

    @Test
    void register_invalidEmail_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setEmail("hathaipat@");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setTerms(true);


        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );


        assertEquals(
                "Invalid email format",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-05
    // Password และ Confirm Password ไม่ตรงกัน
    // ============================================================

    @Test
    void register_passwordMismatch_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setEmail("hathaipat012@gmail.com");
        request.setPassword("123456");
        request.setConfirmPassword("1234567");
        request.setTerms(true);


        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );


        assertEquals(
                "Passwords do not match",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-06
    // ไม่ยอมรับ Terms
    // ============================================================

    @Test
    void register_termsNotAccepted_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setEmail("hathaipat012@gmail.com");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setTerms(false);


        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );


        assertEquals(
                "Terms and Conditions must be accepted",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-07
    // RegisterRequest เป็น null
    // ============================================================

    @Test
    void register_nullRequest_shouldThrowException() {

        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(null)
                );


        assertEquals(
                "Register request is required",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-08
    // ไม่กรอก Password
    // ============================================================

    @Test
    void register_emptyPassword_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setEmail("hathaipat012@gmail.com");
        request.setPassword("");
        request.setConfirmPassword("123456");
        request.setTerms(true);


        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );


        assertEquals(
                "Password is required",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-09
    // ไม่กรอก Confirm Password
    // ============================================================

    @Test
    void register_emptyConfirmPassword_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setEmail("hathaipat012@gmail.com");
        request.setPassword("123456");
        request.setConfirmPassword("");
        request.setTerms(true);


        // Act & Assert
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.register(request)
                );


        assertEquals(
                "Password confirmation is required",
                exception.getMessage()
        );


        verifyNoInteractions(supabaseWebClient);
    }


    // ============================================================
    // TC-JU-01-10
    // Supabase ตอบกลับ HTTP 400
    // ============================================================

    @Test
    void register_supabaseError_shouldThrowException() {

        // Arrange
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Hathaipat");
        request.setLastName("Wisutthitam");
        request.setUsername("hathaipat");
        request.setEmail("hathaipat012@gmail.com");
        request.setRoomNumber("A101");
        request.setPhoneNo("0812345678");
        request.setPassword("123456");
        request.setConfirmPassword("123456");
        request.setTerms(true);


        when(supabaseWebClient.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(
                any(Function.class)
        )).thenReturn(requestBodySpec);

        when(requestBodySpec.contentType(
                MediaType.APPLICATION_JSON
        )).thenReturn(requestBodySpec);

        doReturn(requestHeadersSpec)
                .when(requestBodySpec)
                .bodyValue(any(Map.class));


        /*
         * จำลอง Supabase ตอบ HTTP 400
         */
        when(requestHeadersSpec.exchangeToMono(
                any()
        )).thenAnswer(invocation -> {

            Function<ClientResponse, Mono<String>> function =
                    invocation.getArgument(0);


            // HTTP 400
            when(clientResponse.statusCode())
                    .thenReturn(HttpStatus.BAD_REQUEST);


            // Error response body จาก Supabase
            when(clientResponse.bodyToMono(String.class))
                    .thenReturn(
                            Mono.just(
                                    "Email already registered"
                            )
                    );


            /*
             * สำคัญ:
             * ให้ function ที่ AuthService ส่งเข้า
             * ทำงานกับ ClientResponse จริงที่เรา mock
             */
            return function.apply(clientResponse);
        });


        // Act & Assert
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.register(request)
                );


        /*
         * AuthService ควรเปลี่ยน HTTP Error
         * เป็นข้อความ Supabase Auth Error
         */
        assertEquals(
                "Supabase Auth Error: 400 BAD_REQUEST - Email already registered",
                exception.getMessage()
        );


        // ตรวจสอบว่ามีการเรียก Supabase
        verify(supabaseWebClient)
                .post();

        verify(requestBodyUriSpec)
                .uri(any(Function.class));

        verify(requestBodySpec)
                .contentType(MediaType.APPLICATION_JSON);

        verify(requestBodySpec)
                .bodyValue(any(Map.class));
    }
}