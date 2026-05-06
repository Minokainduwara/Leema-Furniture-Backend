package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // ── Admin: filter users by role / status / search ─────────────────────────
    @Query("""
            SELECT u FROM User u
            WHERE (:role IS NULL OR u.role = :role)
              AND (:status IS NULL OR u.status = :status)
              AND (:search IS NULL
                   OR LOWER(u.name)  LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<User> findWithFilters(
            @Param("role") User.Role role,
            @Param("status") User.Status status,
            @Param("search") String search,
            Pageable pageable);

    // ── Analytics: count by status string ────────────────────────────────────
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") String status);

    // ── Analytics: new users this month ──────────────────────────────────────
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :from AND :to")
    long countByCreatedAtBetween(
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    // ── Analytics: daily new-user counts for the last N days ─────────────────
    @Query(value = """
            SELECT DATE(created_at) AS date, COUNT(*) AS count
            FROM users
            WHERE DATE(created_at) BETWEEN :from AND :to
            GROUP BY DATE(created_at)
            ORDER BY date ASC
            """, nativeQuery = true)
    List<UserGrowthRow> findDailyGrowth(
            @Param("from") java.time.LocalDate from,
            @Param("to")   java.time.LocalDate to);

    /**
     * Projection used by the daily-growth native query above.
     */
    interface UserGrowthRow {
        java.time.LocalDate getDate();
        Long getCount();
    }
}
