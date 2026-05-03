package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Integer cartId,
        List<CartItemResponse> items,
        String couponCode,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal shippingCost,
        BigDecimal discountAmount,
        BigDecimal total
) {
}