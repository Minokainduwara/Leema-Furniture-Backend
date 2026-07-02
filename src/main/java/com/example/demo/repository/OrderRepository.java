package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);
    // 📅 DATE FILTER
    List<Order> findByCreatedAtGreaterThanEqual(LocalDateTime date);
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    BigDecimal getTotalSales();
    Optional<Order> findByOrderNumber(String orderNumber);
    long count();
    int countByUser_Email(String email);
    int countByUser_EmailAndStatus(String email, Order.OrderStatus status);
    List<Order> findTop5ByUser_EmailOrderByCreatedAtDesc(String email);
    long countByStatus(Order.OrderStatus status);
    @Query("""
            SELECT o FROM Order o
            WHERE (:status        IS NULL OR o.status        = :status)
              AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)
              AND (:from          IS NULL OR CAST(o.createdAt AS date) >= :from)
              AND (:to            IS NULL OR CAST(o.createdAt AS date) <= :to)
              AND (:userId        IS NULL OR o.user.id        = :userId)
            """)
    Page<Order> findWithAdminFilters(
            @Param("status")        String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("from")          LocalDate from,
            @Param("to") LocalDate to,
            @Param("userId")        Integer userId,
            Pageable pageable);

    // ── Analytics: counts ─────────────────────────────────────────────────────
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") String status);

    // ── Analytics: platform-wide revenue ─────────────────────────────────────
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'COMPLETED'")
    BigDecimal sumTotalRevenue();
    List<Order> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);
    List<Order> findByCreatedAtAfter(LocalDateTime dateTime);
    List<Order> findByUser_Id(Integer userId);

    // ✅ Search by order number
    List<Order> findByOrderNumberContainingIgnoreCase(String query);

}