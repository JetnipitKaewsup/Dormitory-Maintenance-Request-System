package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.Reporter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AdminReporterRepository extends JpaRepository<Reporter, UUID> {
}