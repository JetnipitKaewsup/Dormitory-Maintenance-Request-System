package com.example.dormitory.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dormitory.model.Reporter;
import com.example.dormitory.model.User;
import com.example.dormitory.repository.ReporterRepository;
import com.example.dormitory.repository.UserRepository;

@Service
public class ReporterProfileServiceImpl
        implements ReporterProfileService {

    private final ReporterRepository reporterRepository;
    private final UserRepository userRepository;

    public ReporterProfileServiceImpl(
            ReporterRepository reporterRepository,
            UserRepository userRepository) {

        this.reporterRepository = reporterRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Reporter getReporterByUserId(UUID userId) {

        return reporterRepository.findByUserUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Reporter not found"));
    }

    @Override
    @Transactional
    public void updatePhone(
            UUID userId,
            String phoneNo) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        user.setPhoneNo(phoneNo);

        userRepository.save(user);
    }
}