package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record WishlistItemResponse(
        Integer id,
        Integer productId,
        String productName,
        LocalDateTime addedAt
) {
}