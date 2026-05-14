package com.example.demo.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_order_number", columnList = "order_number"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_payment_status", columnList = "payment_status"),
                @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Order belongs to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    // Shipping address snapshot reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    @JsonIgnore
    private ShippingAddress shippingAddress;

    // Billing address snapshot reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    @JsonIgnore
    private BillingAddress billingAddress;

    // Shipping method
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id")
    @JsonIgnore
    private ShippingMethod shippingMethod;



    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    // One order has many order items
    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonProperty("items")
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.orderNumber == null || this.orderNumber.isEmpty()) {
            this.orderNumber = generateOrderNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    @ManyToOne
    @JoinColumn(name = "handled_by")
    @JsonIgnore
    private User handledBy;

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }
    @JsonProperty("customerName")
    public String getCustomerName() {
        return user != null ? user.getName() : null;
    }
    @JsonProperty("phone")
    public String getPhone() {
        return user != null ? user.getPhoneNumber() : null;
    }

    @JsonProperty("address")
    public String getAddress() {
        if (billingAddress == null) return null;

        return billingAddress.getStreetAddress() + ", " +
                billingAddress.getCity() + ", " +
                billingAddress.getCountry();
    }
    @JsonProperty("orderDate")
    public LocalDateTime getOrderDate() {
        return createdAt;
    }
    // ================= ENUMS =================

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED,
        RETURNED
    }

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED,
        CANCELLED
    }
}