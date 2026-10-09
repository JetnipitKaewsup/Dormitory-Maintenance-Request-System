package com.example.dormitory.controller.web;

import com.example.dormitory.dto.response.NotificationResponse;
import com.example.dormitory.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/technician/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String notifications(
            Authentication authentication,
            Model model) {

        UUID userId = getUserId(authentication);

        List<NotificationResponse> notifications =
                notificationService.getNotifications(userId);

        long unreadCount =
                notificationService.countUnread(userId);

        model.addAttribute(
                "notifications",
                notifications
        );

        model.addAttribute(
                "unreadCount",
                unreadCount
        );

        return "technician/notification";
    }

    @PostMapping("/{notificationId}/read")
    @ResponseBody
    public void markAsRead(
            @PathVariable UUID notificationId,
            Authentication authentication) {

        UUID userId = getUserId(authentication);

        notificationService.markAsRead(
                notificationId,
                userId
        );
    }

    private UUID getUserId(
            Authentication authentication) {

        com.example.dormitory.domain.entity.User user =
                (com.example.dormitory.domain.entity.User)
                        authentication.getPrincipal();

        return user.getUserId();
    }

    @GetMapping("/data")
    @ResponseBody
    public List<NotificationResponse> getNotificationData(
            Authentication authentication) {

        UUID userId = getUserId(authentication);

        return notificationService.getNotifications(userId);
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public long getUnreadCount(
            Authentication authentication) {

        UUID userId = getUserId(authentication);

        return notificationService.countUnread(userId);
    }
}
