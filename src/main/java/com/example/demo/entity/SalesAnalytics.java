package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_analytics",
        indexes = {
                @Index(name = "idx_date", columnList = "date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // One record per day
    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(name = "total_orders")
    private Integer totalOrders = 0;

    @Column(name = "total_sales", precision = 12, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;

    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "total_customers")
    private Integer totalCustomers = 0;

    @Column(name = "avg_order_value", precision = 10, scale = 2)
    private BigDecimal avgOrderValue = BigDecimal.ZERO;

    @Column(name = "total_items_sold")
    private Integer totalItemsSold = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
