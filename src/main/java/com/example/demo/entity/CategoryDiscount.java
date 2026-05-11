package com.example.demo.entity;

import com.example.demo.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "category_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean active = true;
}