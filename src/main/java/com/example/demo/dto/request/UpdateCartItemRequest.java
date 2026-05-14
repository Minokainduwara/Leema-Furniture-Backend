package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {

    // Accept both camelCase (productId) and snake_case (product_id) so the
    // uiparts Cart.tsx (which posts snake_case) works alongside other callers.
    @JsonAlias({"product_id"})
    @NotNull(message = "productId is required")
    private Integer productId;

    @NotNull(message = "quantity is required")
    @Min(value = 0, message = "quantity must be 0 or more")
    private Integer quantity;
}
