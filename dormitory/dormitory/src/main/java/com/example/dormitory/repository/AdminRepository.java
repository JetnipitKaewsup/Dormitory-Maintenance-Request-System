package com.example.dormitory.repository;

import com.example.dormitory.model.Admin;
import com.example.dormitory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByUser(User user);
}