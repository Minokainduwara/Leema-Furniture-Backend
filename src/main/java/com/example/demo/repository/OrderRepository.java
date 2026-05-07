package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(Integer userId);

    Page<Order> findByUserId(Integer userId, Pageable pageable);
    //List<Order> findByHandledById(Integer userId);
    List<Order> findByHandledById(Integer userId);
    List<Order> findByOrderNumberContainingIgnoreCaseOrUser_NameContainingIgnoreCase(
            String orderNumber,
            String fullName
    );

    // 📊 STATUS FILTER
    List<Order> findByStatus(Order.OrderStatus status);

    // 📅 DATE FILTER
    List<Order> findByCreatedAtGreaterThanEqual(LocalDateTime date);
}