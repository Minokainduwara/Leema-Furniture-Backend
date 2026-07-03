package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(Integer userId);

    Page<Order> findByUserId(Integer userId, Pageable pageable);
    long countByUserId(Integer userId);
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
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal getTotalSales();

    long count();
    int countByUser_Email(String email);
    int countByUser_EmailAndStatus(String email, Order.OrderStatus status);
    List<Order> findTop5ByUser_EmailOrderByCreatedAtDesc(String email);
}