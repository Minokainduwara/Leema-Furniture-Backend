package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(

        Integer id,
        Integer productId,
        String productName,
        String image,
        Integer quantity,
        Integer stock,
        BigDecimal addedPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}