package com.example.dormitory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.example.dormitory.domain.enums.UserRole;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth

                // Public
                .requestMatchers(
                        "/",
                        "/login",
                        "/register",
                        "/forgot",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**")
                .permitAll()

                // Admin
                .requestMatchers("/admin/**")
                .hasRole(UserRole.ADMIN.name())

                // Reporter
                .requestMatchers("/reporter/**")
                .hasRole(UserRole.REPORTER.name())

                // Technician
                .requestMatchers("/technician/**")
                .hasRole(UserRole.TECHNICIAN.name())

                // API
                .requestMatchers("/api/v1/reporters/**")
                .hasRole(UserRole.REPORTER.name())

                .requestMatchers("/api/v1/repair-requests/**")
                .authenticated()

                // Everything else
                .anyRequest()
                .authenticated())

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true));

        return http.build();
    }
}