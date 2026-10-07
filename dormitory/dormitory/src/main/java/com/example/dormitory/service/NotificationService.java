package com.example.dormitory.service;

import com.example.dormitory.dto.response.NotificationResponse;
import com.example.dormitory.event.RepairAssignmentCreatedEvent;
import com.example.dormitory.event.RepairStatusChangedEvent;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void notifyStatusChanged(
            RepairStatusChangedEvent event
    );

    void notifyAssignmentCreated(
            RepairAssignmentCreatedEvent event
    );

    List<NotificationResponse> getNotifications(
            UUID userId
    );

    long countUnread(
            UUID userId
    );

    void markAsRead(
            UUID notificationId,
            UUID userId
    );
}
