package com.example.dormitory.service;
import java.util.UUID;
import com.example.dormitory.domain.entity.Technician;

public interface TechnicianService {

    boolean isTechnician(UUID userId);

    Technician getByUserId(UUID userId);

    String getTechnicianName(UUID userId);
}
