package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public int sendAnnouncement(String title, String message, String audience) {
        List<User> recipients;
        
        switch (audience.toUpperCase()) {
            case "CUSTOMERS":
                recipients = userRepository.findByRole(User.Role.CUSTOMER);
                break;
            case "SELLERS":
                recipients = userRepository.findByRole(User.Role.SELLER);
                break;
            case "ALL":
            default:
                recipients = userRepository.findAll();
                break;
        }
        
        // Create notifications for each recipient
        for (User user : recipients) {
            Notification notification = Notification.builder()
                    .user(user)
                    .title(title)
                    .message(message)
                    .type("ANNOUNCEMENT")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);
        }
        
        return recipients.size();
    }
}