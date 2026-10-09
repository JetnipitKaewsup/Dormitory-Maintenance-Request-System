package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AdminResidentRepository extends JpaRepository<Resident, UUID> {
}