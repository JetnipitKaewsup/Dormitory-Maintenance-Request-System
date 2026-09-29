package com.example.dormitory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dormitory.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}