package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface TechnicianRepository extends JpaRepository<Technician, UUID> {

        Optional<Technician> findByUserUserId(UUID userId);
}
