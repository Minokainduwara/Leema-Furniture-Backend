package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserId(Integer userId);

    long countByUserIdAndIsReadFalse(Integer userId);
    @Query("SELECT n FROM Notification n JOIN FETCH n.user")
    List<Notification> findAllWithUser();
}