package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AdminTechnicianRepository extends JpaRepository<Technician, UUID> {
    Optional<Technician> findByUser(User user);
}