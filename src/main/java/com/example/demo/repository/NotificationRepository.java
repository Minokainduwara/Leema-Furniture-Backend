package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Seller view (ALL notifications)
    List<Notification> findAllByOrderByCreatedAtDesc();

    // Customer view (ONLY their notifications)
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUserId(Integer userId);
    List<Notification> findByUserIdAndOrderNumberContaining(Integer userId, String orderNumber);
    long countByUserIdAndIsReadFalse(Integer userId);
    @Query("SELECT n FROM Notification n JOIN FETCH n.user")
    List<Notification> findAllWithUser();
    List<Notification> findByOrderNumberContainingIgnoreCaseOrderByCreatedAtDesc(String orderNumber);
}