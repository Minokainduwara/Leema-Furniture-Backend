package com.example.demo.controller;

import com.example.demo.entity.Notification;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET /api/notifications?userId=1
    @GetMapping
    public List<Notification> getNotifications(@RequestParam Integer userId) {
        return notificationService.getUserNotifications(userId);
    }

    // PATCH /api/notifications/{id}/read
    @PatchMapping("/{id}/read")
    public String markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }

    // PATCH /api/notifications/read-all?userId=1
    @PatchMapping("/read-all")
    public String markAllAsRead(@RequestParam Integer userId) {
        notificationService.markAllAsRead(userId);
        return "All notifications marked as read";
    }

    // DELETE /api/notifications/{id}
    @DeleteMapping("/{id}")
    public String deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return "Notification deleted";
    }
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }
    // GET /api/notifications/unread-count?userId=1
    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam Integer userId) {
        return notificationService.getUnreadCount(userId);
    }
}