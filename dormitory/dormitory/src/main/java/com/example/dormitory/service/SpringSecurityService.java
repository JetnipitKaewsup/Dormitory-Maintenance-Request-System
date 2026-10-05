package com.example.dormitory.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.dormitory.domain.entity.User;
import com.example.dormitory.repository.UserRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SpringSecurityService {
    private final UserRepository userRepository;

    public SpringSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Authentication createAuthentication(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "ไม่พบผู้ใช้ในระบบ"));

        String role = user.getRole();

        if (role == null || role.isBlank()) {
            throw new IllegalStateException(
                    "ผู้ใช้ยังไม่ได้กำหนด Role");
        }

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + role)));
    }
}
