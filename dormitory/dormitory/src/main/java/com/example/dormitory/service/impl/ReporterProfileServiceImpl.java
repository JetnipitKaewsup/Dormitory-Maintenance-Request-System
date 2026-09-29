
package com.example.dormitory.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dormitory.domain.entity.Reporter;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.repository.ReporterRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.ReporterProfileService;

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

    // ดึงข้อมูล Reporter จาก User ID
    @Override
    @Transactional(readOnly = true)
    public Reporter getReporterByUserId(UUID userId) {

        return reporterRepository.findByUserUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Reporter not found"));
    }

    // แก้ไขเบอร์โทรศัพท์
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