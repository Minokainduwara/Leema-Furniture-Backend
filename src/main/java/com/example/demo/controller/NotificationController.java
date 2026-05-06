package com.example.demo.controller;

import com.example.demo.dto.response.NotificationDTO;
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

    // =========================
    // GET ALL NOTIFICATIONS (DTO)
    // =========================
    @GetMapping
    public List<NotificationDTO> getNotifications() {
        return notificationService.getUserNotifications();
    }

    // =========================
    // MARK AS READ
    // =========================
    @PatchMapping("/{id}/read")
    public String markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return "Notification marked as read";
    }

    // =========================
    // MARK ALL AS READ
    // =========================
    @PatchMapping("/read-all")
    public String markAllAsRead(@RequestParam Integer userId) {
        notificationService.markAllAsRead(userId);
        return "All notifications marked as read";
    }

    // =========================
    // DELETE NOTIFICATION
    // =========================
    @DeleteMapping("/{id}")
    public String deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return "Notification deleted";
    }

    // =========================
    // CREATE NOTIFICATION
    // =========================
    @PostMapping
    public NotificationDTO createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }
    // =========================
    // UNREAD COUNT
    // =========================
    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam Integer userId) {
        return notificationService.getUnreadCount(userId);
    }
}