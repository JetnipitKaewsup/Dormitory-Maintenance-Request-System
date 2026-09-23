package com.example.dormitory.service;

import java.util.UUID;

import com.example.dormitory.model.Reporter;

public interface ReporterProfileService {

    Reporter getReporterByUserId(UUID userId);

    void updatePhone(
            UUID userId,
            String phoneNo);
}