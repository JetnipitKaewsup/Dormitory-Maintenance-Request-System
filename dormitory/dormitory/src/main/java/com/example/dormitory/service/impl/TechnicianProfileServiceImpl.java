package com.example.dormitory.service.impl;

import java.util.UUID;
import org.springframework.stereotype.Service;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.repository.TechnicianRepository;
import com.example.dormitory.service.TechnicianProfileService;

@Service
public class TechnicianProfileServiceImpl
        implements TechnicianProfileService {

    private final TechnicianRepository technicianRepository;

    public TechnicianProfileServiceImpl(
            TechnicianRepository technicianRepository) {

        this.technicianRepository = technicianRepository;
    }

    @Override
    public Technician getTechnicianByUserId(UUID userId) {

        return technicianRepository
                .findByUserUserId(userId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "ไม่พบข้อมูลช่าง"
                        )
                );
    }
}