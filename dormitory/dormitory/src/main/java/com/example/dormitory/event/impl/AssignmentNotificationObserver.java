package com.example.dormitory.event.impl;

import org.springframework.stereotype.Component;

import com.example.dormitory.event.RepairAssignmentCreatedEvent;
import com.example.dormitory.event.RepairAssignmentObserver;
import com.example.dormitory.service.NotificationService;

@Component
public class AssignmentNotificationObserver
        implements RepairAssignmentObserver {

    private final NotificationService notificationService;

    public AssignmentNotificationObserver(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @Override
    public void onAssignmentCreated(
            RepairAssignmentCreatedEvent event) {
                //เทส
                   System.out.println(
            ">>> AssignmentNotificationObserver: received event"
    );
                //เทส
        notificationService.notifyAssignmentCreated(event);
    }
}