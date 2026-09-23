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

    public SupabaseAuthResponse login(LoginRequest request) {

        Map<String, String> body = new HashMap<>();

        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        System.out.println("Login email = " + request.getEmail());

        return supabaseWebClient
                .post()
                .uri("/auth/v1/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response -> {

                    return response.bodyToMono(String.class)
                            .map(responseBody -> {

                                System.out.println(
                                        "Supabase status = "
                                        + response.statusCode()
                                );

                                System.out.println(
                                        "Supabase response = "
                                        + responseBody
                                );

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
                            });
                })
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
        metadata.put("phone_no", request.getPhoneNo());

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