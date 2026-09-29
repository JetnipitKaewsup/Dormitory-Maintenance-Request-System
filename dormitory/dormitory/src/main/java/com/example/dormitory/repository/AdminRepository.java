package com.example.dormitory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dormitory.domain.entity.Admin;
import com.example.dormitory.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByUser(User user);
}