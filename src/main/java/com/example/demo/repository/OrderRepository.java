package com.example.demo.repository;
import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // ── Basic lookups ─────────────────────────────────────────────────────────
    Page<Order> findByUserId(Integer userId, Pageable pageable);

    // ── Admin: full multi-field filter ────────────────────────────────────────
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
            @Param("to")            LocalDate to,
            @Param("userId")        Integer userId,
            Pageable pageable);

    // ── Analytics: counts ─────────────────────────────────────────────────────
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") String status);

    // ── Analytics: platform-wide revenue ─────────────────────────────────────
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'COMPLETED'")
    BigDecimal sumTotalRevenue();

}