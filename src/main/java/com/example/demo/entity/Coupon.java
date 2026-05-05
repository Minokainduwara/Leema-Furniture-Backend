package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coupons",
        indexes = {
                @Index(name = "idx_code", columnList = "code"),
                @Index(name = "idx_is_active", columnList = "is_active"),
                @Index(name = "idx_valid_until", columnList = "valid_until")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "current_uses")
    private Integer currentUses = 0;

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Column(name = "max_order_amount", precision = 10, scale = 2)
    private BigDecimal maxOrderAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_to")
    private ApplicableTo applicableTo = ApplicableTo.all_products;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // User who created the coupon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "coupon",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CouponProduct> couponProducts;

    @OneToMany(mappedBy = "coupon",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CouponCategory> couponCategories;

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

    public enum DiscountType {
        percentage,
        fixed_amount
    }

    public enum ApplicableTo {
        all_products,
        specific_products,
        specific_categories
    }
}
