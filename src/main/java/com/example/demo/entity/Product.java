package com.example.demo.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products",
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

    // Many products belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Many products belong to one seller (user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(unique = true, length = 100)
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "long_description", columnDefinition = "LONGTEXT")
    private String longDescription;

    @Column(length = 500)
    private String image;
    @Column(name = "featured")
    private Boolean featured = false;

    // JSON column (requires converter or Hibernate support)
    @Column(columnDefinition = "JSON")
    private String images;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_sales")
    private Integer totalSales = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<OrderItem> orderItems;

    // Auto timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        setWarrantyBasedOnType();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        setWarrantyBasedOnType();
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProductType type;
    @Column(name = "warranty_years")
    private Integer warrantyYears;

    private void setWarrantyBasedOnType() {
        if (this.type == ProductType.TEKA) {
            this.warrantyYears = 2;
        } else {
            this.warrantyYears = 15;
        }
    }
    // ================= ENUM =================

    public enum ProductStatus {
        ACTIVE,
        INACTIVE,
        DISCONTINUED,
        DRAFT
    }
    public enum ProductType {
        TEKA,
        OTHER
    }
}