package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "low_stock_alerts",
        indexes = {
                @Index(name = "idx_product_id", columnList = "product_id"),
                @Index(name = "idx_alert_sent", columnList = "alert_sent")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Product being monitored
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "threshold_quantity", nullable = false)
    private Integer thresholdQuantity;

    @Column(name = "current_quantity")
    private Integer currentQuantity;

    @Column(name = "alert_sent")
    private Boolean alertSent = false;

    @Column(name = "alert_sent_at")
    private LocalDateTime alertSentAt;

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
