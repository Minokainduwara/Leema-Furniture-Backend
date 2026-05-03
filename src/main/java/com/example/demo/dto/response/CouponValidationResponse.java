package com.example.demo.dto.response;

import java.math.BigDecimal;

public record CouponValidationResponse(
        String couponCode,
        boolean valid,
        String message,
        String discountType,
        BigDecimal discountValue,
        BigDecimal discountAmount,
        BigDecimal totalAfterDiscount
) {
}