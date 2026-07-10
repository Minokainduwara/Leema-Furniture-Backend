package com.example.demo.service;

import com.example.demo.dto.response.NotificationDTO;
import com.example.demo.entity.Notification;
import com.example.demo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // 🔵 GET ALL (DTO version)
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public List<NotificationDTO> searchByOrderForSeller(String orderNumber) {
        return notificationRepository
                .findByOrderNumberContainingIgnoreCaseOrderByCreatedAtDesc(orderNumber)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public List<NotificationDTO> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 🔵 CREATE (still entity input is OK, but return DTO recommended)
    public NotificationDTO createNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        return mapToDTO(saved);
    }

    // 🔵 MARK AS READ
    public void markAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // 🔵 MARK ALL AS READ
    public void markAllAsRead(Integer userId) {
        List<Notification> list = notificationRepository.findByUserId(userId);

        for (Notification n : list) {
            n.setIsRead(true);
        }

        notificationRepository.saveAll(list);
    }

    // 🔵 DELETE
    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }

    // 🔵 UNREAD COUNT (no change needed)
    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // =========================
    // 🔥 MAPPING FUNCTION
    // =========================
    private NotificationDTO mapToDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();

        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setRead(n.getIsRead());
        dto.setOrderNumber(n.getOrderNumber());
        dto.setCreatedAt(n.getCreatedAt());
        if (n.getUser() != null) {
            dto.setUserId(n.getUser().getId());
            dto.setCustomerName(n.getUser().getName());
            dto.setCustomerEmail(n.getUser().getEmail());
        }

        return dto;
    }
    public List<NotificationDTO> searchByOrder(Integer userId, String orderNumber) {
        return notificationRepository
                .findByUserIdAndOrderNumberContaining(userId, orderNumber)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}