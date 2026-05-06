package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
        Integer id,
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal addedPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}