package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CartItemRequest(

	@NotNull(message = "productId is required")
	@Positive(message = "productId must be greater than 0")
	Integer productId,

	@NotNull(message = "quantity is required")
	@Positive(message = "quantity must be greater than 0")
	Integer quantity,

	@NotNull(message = "addedPrice is required")
	@DecimalMin(value = "0.0", inclusive = true, message = "addedPrice must be greater than or equal to 0")
	BigDecimal addedPrice
) {
}
