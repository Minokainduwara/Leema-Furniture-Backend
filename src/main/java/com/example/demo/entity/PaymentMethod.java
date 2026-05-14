package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_method_type", columnList = "method_type"),
                @Index(name = "idx_is_default", columnList = "is_default")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Payment method belongs to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_type", nullable = false)
    private MethodType methodType;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    // Card details (safe partial storage only)
    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "card_brand", length = 50)
    private String cardBrand;

    @Column(name = "card_expiry_month")
    private Integer cardExpiryMonth;

    @Column(name = "card_expiry_year")
    private Integer cardExpiryYear;

    // PayPal or payment email
    @Column(length = 255)
    private String email;

    // Bank transfer fields
    @Column(name = "account_holder", length = 255)
    private String accountHolder;

    @Column(name = "bank_name", length = 255)
    private String bankName;

    @Column(name = "account_number_last_four", length = 4)
    private String accountNumberLastFour;

    // Stripe tokenized payment method
    @Column(name = "stripe_payment_method_id", length = 255)
    private String stripePaymentMethodId;

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

    public enum MethodType {
        credit_card,
        debit_card,
        paypal,
        stripe,
        bank_transfer,
        apple_pay,
        google_pay
    }
}