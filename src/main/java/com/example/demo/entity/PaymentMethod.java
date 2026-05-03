package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_type", nullable = false)
    private MethodType methodType;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "card_brand", length = 50)
    private String cardBrand;

    @Column(name = "card_expiry_month")
    private Integer cardExpiryMonth;

    @Column(name = "card_expiry_year")
    private Integer cardExpiryYear;

    @Column(length = 255)
    private String email;

    @Column(name = "account_holder", length = 255)
    private String accountHolder;

    @Column(name = "bank_name", length = 255)
    private String bankName;

    @Column(name = "account_number_last_four", length = 4)
    private String accountNumberLastFour;

    @Column(name = "stripe_payment_method_id", length = 255)
    private String stripePaymentMethodId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MethodType {
        credit_card, debit_card, paypal, stripe, bank_transfer, apple_pay, google_pay
    }
}
