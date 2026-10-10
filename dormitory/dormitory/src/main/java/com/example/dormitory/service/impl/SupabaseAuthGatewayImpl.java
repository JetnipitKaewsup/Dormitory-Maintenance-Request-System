package com.example.dormitory.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dormitory.dto.request.RegisterRequest;
import com.example.dormitory.dto.response.SupabaseAuthResponse;
import com.example.dormitory.service.SupabaseAuthGateway;

import tools.jackson.databind.ObjectMapper;

@Service
public class SupabaseAuthGatewayImpl
        implements SupabaseAuthGateway {

    private final WebClient supabaseWebClient;
    private final ObjectMapper objectMapper;

    public SupabaseAuthGatewayImpl(
            WebClient supabaseWebClient,
            ObjectMapper objectMapper) {

        this.supabaseWebClient = supabaseWebClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public SupabaseAuthResponse login(
            String email,
            String password) {

        Map<String, String> body = Map.of(
                "email", email,
                "password", password
        );

        String responseBody = supabaseWebClient
                .post()
                .uri("/auth/v1/token?grant_type=password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(content -> {
                                    if (response.statusCode().isError()) {
                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + response.statusCode()
                                                        + " - " + content);
                                    }
                                    return content;
                                }))
                .block();

        try {
            return objectMapper.readValue(
                    responseBody,
                    SupabaseAuthResponse.class);
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Cannot parse Supabase response",
                    exception);
        }
    }

    @Override
    public void register(RegisterRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("email", request.getEmail());
        metadata.put("first_name", request.getFirstName());
        metadata.put("last_name", request.getLastName());
        metadata.put("username", request.getUsername());
        metadata.put("room_number", request.getRoomNumber());
        metadata.put("phone_no", request.getPhoneNo());

        body.put("data", metadata);

        String responseBody = supabaseWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/v1/signup")
                        .queryParam(
                                "redirect_to",
                                "http://localhost:8080/auth/verified")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(content -> {
                                    if (response.statusCode().isError()) {
                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + response.statusCode()
                                                        + " - " + content);
                                    }
                                    return content;
                                }))
                .block();
    }

    @Override
    public void forgotPassword(String email) {

        Map<String, String> body = Map.of(
                "email", email
        );

        supabaseWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/v1/recover")
                        .queryParam(
                                "redirect_to",
                                "http://localhost:8080/reset-password")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(content -> {
                                    if (response.statusCode().isError()) {
                                        throw new RuntimeException(
                                                "Supabase Auth Error: "
                                                        + response.statusCode()
                                                        + " - " + content);
                                    }
                                    return content;
                                }))
                .block();
    }

    @Override
    public void resetPassword(
            String accessToken,
            String newPassword) {

        Map<String, String> body = Map.of(
                "password", newPassword
        );

        supabaseWebClient
                .put()
                .uri("/auth/v1/user")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(content -> {
                                    if (response.statusCode().isError()) {
                                        throw new RuntimeException(
                                                "Supabase Password Reset Error: "
                                                        + response.statusCode()
                                                        + " - " + content);
                                    }
                                    return content;
                                }))
                .block();
    }
}
