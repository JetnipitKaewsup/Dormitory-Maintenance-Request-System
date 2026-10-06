package com.example.dormitory.service;

import java.util.UUID;
import com.example.dormitory.domain.entity.Technician;

public interface TechnicianProfileService {

    Technician getTechnicianByUserId(UUID userId);
}
