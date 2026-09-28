
package com.example.dormitory.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.dto.LoginRequest;
import com.example.dormitory.dto.RegisterRequest;
import com.example.dormitory.dto.SupabaseAuthResponse;

import tools.jackson.databind.ObjectMapper;

@Service
public class AuthService {

    private final WebClient supabaseWebClient;

    public AuthService(WebClient supabaseWebClient) {
        this.supabaseWebClient = supabaseWebClient;
    }

    // ============================================================
    // LOGIN
    // ============================================================

    public SupabaseAuthResponse login(LoginRequest request) {

        Map<String, String> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        return supabaseWebClient
                .post()
                .uri("/auth/v1/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .map(responseBody -> {

                                    if (response.statusCode().isError()) {
                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + responseBody
                                        );
                                    }

                                    try {
                                        ObjectMapper mapper =
                                                new ObjectMapper();

                                        return mapper.readValue(
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
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        );
    }

    // ============================================================
    // FORGOT PASSWORD
    // ============================================================

    public void forgotPassword(String email) {

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