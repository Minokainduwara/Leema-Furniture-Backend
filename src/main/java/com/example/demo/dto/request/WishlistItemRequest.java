package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WishlistItemRequest(
                @NotNull(message = "productId is required")
                @Positive(message = "productId must be greater than 0")
        Integer productId
) {
}