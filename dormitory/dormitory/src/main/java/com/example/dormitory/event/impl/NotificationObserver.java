package com.example.dormitory.event.impl;

import org.springframework.stereotype.Component;

import com.example.dormitory.event.RepairStatusChangedEvent;
import com.example.dormitory.event.RepairStatusObserver;
import com.example.dormitory.service.NotificationService;

@Component
public class NotificationObserver implements RepairStatusObserver {

    private final NotificationService notificationService;

    public NotificationObserver(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onStatusChanged(
            RepairStatusChangedEvent event) {
                //เทส
                    System.out.println(
            ">>> NotificationObserver: received event"
    );
                //เทส
        notificationService.notifyStatusChanged(event);
    }
}