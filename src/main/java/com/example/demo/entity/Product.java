package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_category_id", columnList = "category_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_sku", columnList = "sku"),
                @Index(name = "idx_price", columnList = "price")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    // =================================================
    // CATEGORY
    // =================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // =================================================
    // PRICING
    // =================================================

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    // =================================================
    // INVENTORY
    // =================================================

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(unique = true, length = 100)
    private String sku;

    // =================================================
    // PRODUCT WEIGHT
    // =================================================

    @Column(name = "weight_kg", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal weightKg = BigDecimal.ZERO;

    // =================================================
    // DESCRIPTIONS
    // =================================================

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "long_description", columnDefinition = "LONGTEXT")
    private String longDescription;

    // =================================================
    // IMAGES
    // =================================================

    @Column(length = 500)
    private String image;

    @Column(columnDefinition = "JSON")
    private String images;

    // =================================================
    // FEATURED
    // =================================================

    @Column(name = "featured")
    @Builder.Default
    private Boolean featured = false;

    // =================================================
    // STATUS
    // =================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    // =================================================
    // RATINGS & SALES
    // =================================================

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_sales")
    @Builder.Default
    private Integer totalSales = 0;

    // =================================================
    // TIMESTAMPS
    // =================================================

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // =================================================
    // RELATIONSHIPS
    // =================================================

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderItem> orderItems;

    // =================================================
    // AUTO TIMESTAMPS
    // =================================================

    @PrePersist
    protected void onCreate() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();
    }

    // =================================================
    // ENUM
    // =================================================

    public enum ProductStatus {
        ACTIVE,
        INACTIVE,
        DISCONTINUED,
        DRAFT
    }
}