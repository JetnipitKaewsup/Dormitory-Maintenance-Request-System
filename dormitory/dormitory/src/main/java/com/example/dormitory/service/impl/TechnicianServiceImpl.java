package com.example.dormitory.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.dormitory.domain.entity.Technician;
import com.example.dormitory.repository.TechnicianRepository;
import com.example.dormitory.service.TechnicianService;

@Service
public class TechnicianServiceImpl
        implements TechnicianService {

    private final TechnicianRepository technicianRepository;

    public TechnicianServiceImpl(
            TechnicianRepository technicianRepository
    ) {
        this.technicianRepository = technicianRepository;
    }

    @Override
    public boolean isTechnician(UUID userId) {

        return technicianRepository
                .findByUserUserId(userId)
                .isPresent();
    }

    @Override
    public Technician getByUserId(UUID userId) {

        return technicianRepository
                .findByUserUserId(userId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "ไม่พบข้อมูลช่าง"
                        )
                );
    }

    @Override
    public String getTechnicianName(UUID userId) {

        Technician technician =
                getByUserId(userId);

        return technician.getUser().getFirstName()
                + " "
                + technician.getUser().getLastName();
    }
}
