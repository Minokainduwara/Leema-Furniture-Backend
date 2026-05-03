package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "coupons")
public class Coupon {

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    public enum ApplicableTo {
        ALL_PRODUCTS,
        SPECIFIC_PRODUCTS,
        SPECIFIC_CATEGORIES
    }

    @Converter(autoApply = false)
    public static class DiscountTypeConverter implements AttributeConverter<DiscountType, String> {
        @Override
        public String convertToDatabaseColumn(DiscountType attribute) {
            if (attribute == null) return null;
            return switch (attribute) {
                case PERCENTAGE -> "percentage";
                case FIXED_AMOUNT -> "fixed_amount";
            };
        }

        @Override
        public DiscountType convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;
            return switch (dbData.toLowerCase()) {
                case "percentage" -> DiscountType.PERCENTAGE;
                case "fixed_amount" -> DiscountType.FIXED_AMOUNT;
                default -> throw new IllegalArgumentException("Unknown discount_type value: " + dbData);
            };
        }
    }

    @Converter(autoApply = false)
    public static class ApplicableToConverter implements AttributeConverter<ApplicableTo, String> {
        @Override
        public String convertToDatabaseColumn(ApplicableTo attribute) {
            if (attribute == null) return null;
            return switch (attribute) {
                case ALL_PRODUCTS -> "all_products";
                case SPECIFIC_PRODUCTS -> "specific_products";
                case SPECIFIC_CATEGORIES -> "specific_categories";
            };
        }

        @Override
        public ApplicableTo convertToEntityAttribute(String dbData) {
            if (dbData == null) return null;
            return switch (dbData.toLowerCase()) {
                case "all_products" -> ApplicableTo.ALL_PRODUCTS;
                case "specific_products" -> ApplicableTo.SPECIFIC_PRODUCTS;
                case "specific_categories" -> ApplicableTo.SPECIFIC_CATEGORIES;
                default -> throw new IllegalArgumentException("Unknown applicable_to value: " + dbData);
            };
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Convert(converter = DiscountTypeConverter.class)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    private Integer maxUses;

    @Column(name = "current_uses", nullable = false)
    private Integer currentUses = 0;

    private BigDecimal minOrderAmount;
    private BigDecimal maxOrderAmount;

    @Convert(converter = ApplicableToConverter.class)
    private ApplicableTo applicableTo = ApplicableTo.ALL_PRODUCTS;

    private LocalDate validFrom;
    private LocalDate validUntil;

    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponProduct> couponProducts = new ArrayList<>();

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponCategory> couponCategories = new ArrayList<>();

    public Coupon() {
    }
}