package com.example.dormitory.repository;

import com.example.dormitory.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    List<Notification> findByRecipient_UserIdOrderByCreatedAtDesc(
            UUID user_id
    );

    long countByRecipient_UserIdAndReadFalse(
            UUID user_id
    );

    Optional<Notification> findByNotificationIdAndRecipient_UserId(
            UUID notification_id,
            UUID user_id
    );
}
