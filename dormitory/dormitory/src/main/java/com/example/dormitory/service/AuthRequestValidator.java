package com.example.dormitory.service;

import org.springframework.stereotype.Component;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.request.RegisterRequest;

@Component
public class AuthRequestValidator {

    public void validateLogin(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Login request is required");
        }

        validateEmail(request.getEmail());

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required");
        }
    }

    public void validateRegister(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Register request is required");
        }

        if (request.getFirstName() == null
                || request.getFirstName().isBlank()) {
            throw new IllegalArgumentException(
                    "First Name is required");
        }

        validateEmail(request.getEmail());

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required");
        }

        if (request.getConfirmPassword() == null
                || request.getConfirmPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Password confirmation is required");
        }

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "Passwords do not match");
        }

        if (!request.isTerms()) {
            throw new IllegalArgumentException(
                    "Terms and Conditions must be accepted");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required");
        }

        if (!email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Invalid email format");
        }
    }

    public void validateResetPassword(
            String accessToken,
            String newPassword) {

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Reset token is required");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required");
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must be at least 6 characters");
        }
    }
}
