
package com.example.dormitory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dormitory.dto.request.RegisterRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegisterTest {

    @Mock
    private SupabaseAuthGateway supabaseAuthGateway;

    private AuthRequestValidator authRequestValidator;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        // ใช้ Validator จริงเพื่อทดสอบเงื่อนไข Validation
        authRequestValidator = new AuthRequestValidator();

        // สร้าง AuthService โดย Inject Gateway Mock
        authService = new AuthService(
                supabaseAuthGateway,
                authRequestValidator
        );
    }

    // ============================================================
    // TC-JU-01-01
    // สมัครสมาชิกด้วยข้อมูลที่ถูกต้อง
    // ============================================================

    @Test
    void register_validData_shouldSuccess() {
        RegisterRequest request = createValidRequest();

        assertDoesNotThrow(() -> authService.register(request));

        verify(supabaseAuthGateway).register(request);
    }

    // ============================================================
    // TC-JU-01-02
    // ไม่กรอก First Name
    // ============================================================

    @Test
    void register_emptyFirstName_shouldThrowException() {
        RegisterRequest request = createValidRequest();
        request.setFirstName("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "First Name is required",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-03
    // ไม่กรอก Email
    // ============================================================

    @Test
    void register_emptyEmail_shouldThrowException() {
        RegisterRequest request = createValidRequest();
        request.setEmail("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Email is required",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-04
    // Email ผิดรูปแบบ
    // ============================================================

    @Test
    void register_invalidEmail_shouldThrowException() {
        RegisterRequest request = createValidRequest();
        request.setEmail("hathaipat@");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Invalid email format",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-05
    // Password และ Confirm Password ไม่ตรงกัน
    // ============================================================

    @Test
    void register_passwordMismatch_shouldThrowException() {
        RegisterRequest request = createValidRequest();
        request.setConfirmPassword("1234567");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Passwords do not match",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-06
    // ไม่ยอมรับ Terms and Conditions
    // ============================================================

    @Test
    void register_termsNotAccepted_shouldThrowException() {
        RegisterRequest request = createValidRequest();
        request.setTerms(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Terms and Conditions must be accepted",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-07
    // RegisterRequest เป็น null
    // ============================================================

    @Test
    void register_nullRequest_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(null)
        );

        assertEquals(
                "Register request is required",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-08
    // ไม่กรอก Password
    // ============================================================

    @Test
    void register_emptyPassword_shouldThrowException() {
        RegisterRequest request = createValidRequest();
        request.setPassword("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Password is required",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-09
    // ไม่กรอก Confirm Password
    // ============================================================

    @Test
    void register_emptyConfirmPassword_shouldThrowException() {
        RegisterRequest request = createValidRequest();
        request.setConfirmPassword("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Password confirmation is required",
                exception.getMessage()
        );

        verifyNoInteractions(supabaseAuthGateway);
    }

    // ============================================================
    // TC-JU-01-10
    // Gateway ส่ง Exception กลับมา
    // ============================================================

    @Test
    void register_supabaseError_shouldThrowException() {
        RegisterRequest request = createValidRequest();

        RuntimeException gatewayException = new RuntimeException(
                "Supabase Auth Error: 400 BAD_REQUEST - Email already registered"
        );

        doThrow(gatewayException)
                .when(supabaseAuthGateway)
                .register(request);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Supabase Auth Error: 400 BAD_REQUEST - Email already registered",
                exception.getMessage()
        );

        verify(supabaseAuthGateway).register(request);
    }

    // ============================================================
    // Helper: สร้างข้อมูลสมัครสมาชิกที่ถูกต้อง
    // ============================================================

    private RegisterRequest createValidRequest() {
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

        return request;
    }
}
