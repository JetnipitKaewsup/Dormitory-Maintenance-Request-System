package com.example.dormitory.service;

import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;

public interface SupabaseAuthGateway {

    SupabaseAuthResponse login(
            String email,
            String password);

    void register(RegisterRequest request);

    void forgotPassword(String email);

    void resetPassword(
            String accessToken,
            String newPassword);
}
