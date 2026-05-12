package com.example.demo.entity;

import com.example.demo.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENTAGE or FIXED

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean active = true;
}