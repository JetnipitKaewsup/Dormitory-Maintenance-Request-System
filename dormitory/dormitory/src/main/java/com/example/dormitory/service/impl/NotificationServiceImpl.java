package com.example.dormitory.service.impl;

import com.example.dormitory.domain.entity.Notification;
import com.example.dormitory.domain.entity.User;
import com.example.dormitory.domain.enums.NotificationType;
import com.example.dormitory.dto.response.NotificationResponse;
import com.example.dormitory.event.RepairAssignmentCreatedEvent;
import com.example.dormitory.event.RepairStatusChangedEvent;
import com.example.dormitory.repository.NotificationRepository;
import com.example.dormitory.repository.UserRepository;
import com.example.dormitory.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void notifyAssignmentCreated(
            RepairAssignmentCreatedEvent event) {

        User technician =
                userRepository.findById(
                        event.technicianUserId()
                ).orElseThrow(() ->
                        new IllegalStateException(
                                "ไม่พบ User ของ Technician"
                        )
                );

        Notification notification =
                new Notification(
                        technician,
                        NotificationType.NEW_ASSIGNMENT,
                        "งานใหม่",
                        "คุณได้รับมอบหมายงานซ่อมใหม่",
                        event.assignmentId()
                );

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getNotifications(
            UUID userId) {

        return notificationRepository
                .findByRecipient_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification ->
                        new NotificationResponse(
                                notification.getNotificationId(),
                                notification.getType().name(),
                                notification.getTitle(),
                                notification.getMessage(),
                                notification.getReferenceId(),
                                notification.isRead(),
                                notification.getCreatedAt()
                        )
                )
                .toList();
    }

    @Override
    public long countUnread(UUID userId) {

        return notificationRepository
                .countByRecipient_UserIdAndReadFalse(userId);
    }

    @Override
    public void markAsRead(
            UUID notificationId,
            UUID userId) {

        Notification notification =
                notificationRepository
                        .findByNotificationIdAndRecipient_UserId(
                                notificationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "ไม่พบ Notification"
                                )
                        );

        notification.markAsRead();
    }

    @Override
    public void notifyStatusChanged(
            RepairStatusChangedEvent event) {

        // ส่วนนี้เราจะต่อ notification ของ Admin/Reporter
        // ในขั้นถัดไป
    }
}
