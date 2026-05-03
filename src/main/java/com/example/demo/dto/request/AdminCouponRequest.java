package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminCouponRequest {
    @NotBlank
    private String code;
    private String description;
    @NotBlank private String discountType;
    @NotNull
    @DecimalMin("0.01") private BigDecimal discountValue;
    private Integer maxUses;
    private BigDecimal minOrderAmount;
    private BigDecimal maxOrderAmount;
    private String applicableTo;
    @NotBlank private String validFrom;
    @NotBlank private String validUntil;
    private boolean isActive = true;
    private java.util.List<Integer> productIds;
    private java.util.List<Integer> categoryIds;
}
