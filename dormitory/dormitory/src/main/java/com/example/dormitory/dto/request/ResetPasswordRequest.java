package com.example.dormitory.dto.request;

public record ResetPasswordRequest(
        String accessToken,
        String password,
        String confirmPassword
) {
}