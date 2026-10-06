package com.example.dormitory.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.dto.request.LoginRequest;
import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;

import tools.jackson.databind.ObjectMapper;

@Service
public class AuthService {

    private final WebClient supabaseWebClient;
    private final ObjectMapper objectMapper;

    public AuthService(
            WebClient supabaseWebClient,
            ObjectMapper objectMapper) {

        this.supabaseWebClient = supabaseWebClient;
        this.objectMapper = objectMapper;
    }

    // =========================================================
    // LOGIN
    // =========================================================

    public SupabaseAuthResponse login(
            LoginRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Login request is required"
            );
        }

        if (request.getEmail() == null
                || request.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (!isValidEmail(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Invalid email format"
            );
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        Map<String, String> body =
                Map.of(
                        "email",
                        request.getEmail(),
                        "password",
                        request.getPassword()
                );

        return supabaseWebClient
                .post()
                .uri(
                        "/auth/v1/token?grant_type=password"
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(responseBody -> {

                                    if (response.statusCode()
                                            .isError()) {

                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + response.statusCode()
                                                        + " - "
                                                        + responseBody
                                        );
                                    }

                                    try {

                                        return objectMapper.readValue(
                                                responseBody,
                                                SupabaseAuthResponse.class
                                        );

                                    } catch (Exception e) {

                                        throw new RuntimeException(
                                                "Cannot parse Supabase response",
                                                e
                                        );
                                    }
                                })
                )
                .block();
    }

    // =========================================================
    // REGISTER
    // =========================================================

    public void register(
            RegisterRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Register request is required"
            );
        }

        if (request.getFirstName() == null
                || request.getFirstName().isBlank()) {

            throw new IllegalArgumentException(
                    "First Name is required"
            );
        }

        if (request.getEmail() == null
                || request.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (!isValidEmail(request.getEmail())) {

            throw new IllegalArgumentException(
                    "Invalid email format"
            );
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        if (request.getConfirmPassword() == null
                || request.getConfirmPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Password confirmation is required"
            );
        }

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        if (!request.isTerms()) {

            throw new IllegalArgumentException(
                    "Terms and Conditions must be accepted"
            );
        }

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "email",
                request.getEmail()
        );

        body.put(
                "password",
                request.getPassword()
        );

        Map<String, Object> metadata =
                new HashMap<>();

        metadata.put(
                "email",
                request.getEmail()
        );

        metadata.put(
                "first_name",
                request.getFirstName()
        );

        metadata.put(
                "last_name",
                request.getLastName()
        );

        metadata.put(
                "username",
                request.getUsername()
        );

        metadata.put(
                "room_number",
                request.getRoomNumber()
        );

        metadata.put(
                "phone_no",
                request.getPhoneNo()
        );

        body.put(
                "data",
                metadata
        );

        supabaseWebClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/auth/v1/signup")
                                .queryParam(
                                        "redirect_to",
                                        "http://localhost:8080/auth/verified"
                                )
                                .build()
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(responseBody -> {

                                    System.out.println();
                                    System.out.println(
                                            "=============================================="
                                    );
                                    System.out.println(
                                            "        SUPABASE REGISTER RESPONSE"
                                    );
                                    System.out.println(
                                            "=============================================="
                                    );
                                    System.out.println(
                                            "HTTP STATUS : "
                                                    + response.statusCode()
                                    );
                                    System.out.println(
                                            "RESPONSE BODY:"
                                    );
                                    System.out.println(
                                            responseBody
                                    );
                                    System.out.println(
                                            "=============================================="
                                    );
                                    System.out.println();

                                    if (response.statusCode()
                                            .isError()) {

                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + response.statusCode()
                                                        + " - "
                                                        + responseBody
                                        );
                                    }

                                    return responseBody;
                                })
                )
                .block();
    }

    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    public void forgotPassword(
            String email) {

        if (email == null
                || email.isBlank()) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (!isValidEmail(email)) {

            throw new IllegalArgumentException(
                    "Invalid email format"
            );
        }

        Map<String, String> body =
                new HashMap<>();

        body.put(
                "email",
                email
        );

        supabaseWebClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/auth/v1/recover")
                                .queryParam(
                                        "redirect_to",
                                        "http://localhost:8080/reset-password"
                                )
                                .build()
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(responseBody -> {

                                    if (response.statusCode()
                                            .isError()) {

                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + response.statusCode()
                                                        + " - "
                                                        + responseBody
                                        );
                                    }

                                    return responseBody;
                                })
                )
                .block();
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    public void resetPassword(
            String accessToken,
            String newPassword) {

        if (accessToken == null
                || accessToken.isBlank()) {

            throw new IllegalArgumentException(
                    "Reset token is required"
            );
        }

        if (newPassword == null
                || newPassword.isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        if (newPassword.length() < 6) {

            throw new IllegalArgumentException(
                    "Password must be at least 6 characters"
            );
        }

        Map<String, String> body =
                Map.of(
                        "password",
                        newPassword
                );

        supabaseWebClient
                .put()
                .uri("/auth/v1/user")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(responseBody -> {

                                    if (response.statusCode()
                                            .isError()) {

                                        throw new RuntimeException(
                                                "Supabase Password Reset Error: "
                                                        + response.statusCode()
                                                        + " - "
                                                        + responseBody
                                        );
                                    }

                                    return responseBody;
                                })
                )
                .block();
    }

    // =========================================================
    // EMAIL VALIDATION
    // =========================================================

    private boolean isValidEmail(
            String email) {

        return email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }
}