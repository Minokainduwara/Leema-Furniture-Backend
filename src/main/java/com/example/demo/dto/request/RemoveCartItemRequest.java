package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemoveCartItemRequest {

    @NotNull(message = "Product ID is required")
    private Integer productId;

}