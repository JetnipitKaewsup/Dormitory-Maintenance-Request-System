
package com.example.dormitory.service;

import java.util.HashMap;
import java.util.Map;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(WebClient supabaseWebClient) {
        this.supabaseWebClient = supabaseWebClient;
    }

    // ============================================================
    // LOGIN
    // ============================================================

    public SupabaseAuthResponse login(LoginRequest request) {

        // Validate request
        if (request == null) {
            throw new IllegalArgumentException(
                    "Login request is required"
            );
        }

        // Validate email
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

        // Validate password
        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        // Prepare request body
        Map<String, String> body = Map.of(
                "email", request.getEmail(),
                "password", request.getPassword()
        );

        // Send request to Supabase Auth
        return supabaseWebClient
                .post()
                .uri("/auth/v1/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(responseBody -> {

                                    if (response.statusCode().isError()) {
                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
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

    // ============================================================
    // REGISTER
    // ============================================================

    public void register(RegisterRequest request) {

        // Validate request
        if (request == null) {
            throw new IllegalArgumentException(
                    "Register request is required"
            );
        }

        // Validate first name
        if (request.getFirstName() == null
                || request.getFirstName().isBlank()) {
            throw new IllegalArgumentException(
                    "First Name is required"
            );
        }

        // Validate email
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

        // Validate password
        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

        // Validate confirm password
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

        // Validate terms
        if (!request.isTerms()) {
            throw new IllegalArgumentException(
                    "Terms and Conditions must be accepted"
            );
        }

        // Supabase Auth request body
        Map<String, Object> body = new HashMap<>();

        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        // User metadata
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("email", request.getEmail());
        metadata.put("first_name", request.getFirstName());
        metadata.put("last_name", request.getLastName());
        metadata.put("username", request.getUsername());
        metadata.put("room_number", request.getRoomNumber());
        metadata.put("phone_no", request.getPhoneNo());

        body.put("data", metadata);

        // Send request to Supabase Auth
        supabaseWebClient
                .post()
                .uri("/auth/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // ============================================================
    // EMAIL VALIDATION
    // ============================================================

    private boolean isValidEmail(String email) {
        return email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }

    // ============================================================
    // FORGOT PASSWORD
    // ============================================================

    public void forgotPassword(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(
                    "Invalid email format"
            );
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        supabaseWebClient
                .post()
                .uri("/auth/v1/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(responseBody -> {

                                    if (response.statusCode().isError()) {
                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + responseBody
                                        );
                                    }

                                    return responseBody;
                                })
                )
                .block();
    }
}