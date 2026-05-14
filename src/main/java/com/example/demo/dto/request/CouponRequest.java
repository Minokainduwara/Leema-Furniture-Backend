package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record CouponRequest(
	@NotBlank(message = "couponCode is required")
	String couponCode,

	@DecimalMin(value = "0.0", inclusive = true, message = "subtotal must be greater than or equal to 0")
	BigDecimal subtotal,
	List<Integer> productIds,
	List<Integer> categoryIds
) {
}
