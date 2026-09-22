package com.example.dormitory.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.dto.LoginRequest;
import com.example.dormitory.dto.RegisterRequest;
import com.example.dormitory.dto.SupabaseAuthResponse;

@Service
public class AuthService {

    private final WebClient supabaseWebClient;

    public AuthService(WebClient supabaseWebClient) {
        this.supabaseWebClient = supabaseWebClient;
    }

    public SupabaseAuthResponse login(LoginRequest request) {

        Map<String, String> body = new HashMap<>();

        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        return supabaseWebClient
                .post()
                .uri("/auth/v1/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(SupabaseAuthResponse.class)
                .block();
    }

    public void register(RegisterRequest request) {

        if (!request.getPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        Map<String, Object> body = new HashMap<>();

        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("first_name", request.getFirstName());
        metadata.put("last_name", request.getLastName());
        metadata.put("username", request.getUsername());
        metadata.put("room_number", request.getRoomNumber());

        body.put("data", metadata);

        supabaseWebClient
                .post()
                .uri("/auth/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}