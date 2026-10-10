package com.example.dormitory.service;

import org.springframework.stereotype.Service;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;

@Service
public class AuthService {

    private final SupabaseAuthGateway supabaseAuthGateway;
    private final AuthRequestValidator authRequestValidator;

    public AuthService(
            SupabaseAuthGateway supabaseAuthGateway,
            AuthRequestValidator authRequestValidator) {

        this.supabaseAuthGateway = supabaseAuthGateway;
        this.authRequestValidator = authRequestValidator;
    }

    // =========================================================
    // LOGIN
    // =========================================================

    public SupabaseAuthResponse login(LoginRequest request) {

        authRequestValidator.validateLogin(request);

        return supabaseAuthGateway.login(
                request.getEmail(),
                request.getPassword());
    }

    // =========================================================
    // REGISTER
    // =========================================================

    public void register(RegisterRequest request) {

        authRequestValidator.validateRegister(request);

        supabaseAuthGateway.register(request);
    }

    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    public void forgotPassword(String email) {

        authRequestValidator.validateEmail(email);

        supabaseAuthGateway.forgotPassword(email);
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    public void resetPassword(
            String accessToken,
            String newPassword) {

        authRequestValidator.validateResetPassword(
                accessToken,
                newPassword);

        supabaseAuthGateway.resetPassword(
                accessToken,
                newPassword);
    }
}
