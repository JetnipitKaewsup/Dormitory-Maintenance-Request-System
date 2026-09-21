package com.example.dormitory.repository;

import com.example.dormitory.model.RepairRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RepairRequestRepository extends JpaRepository<RepairRequest, UUID> {
}