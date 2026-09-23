package com.example.dormitory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dormitory.model.Reporter;
/**
 * ReporterRepository
 */
public interface ReporterRepository extends JpaRepository<Reporter,UUID>{

    Optional<Reporter> findByUserUserId(UUID userId);
}