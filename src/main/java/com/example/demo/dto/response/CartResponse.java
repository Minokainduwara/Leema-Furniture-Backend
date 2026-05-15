package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private List<CartItemResponse> items;

    // subtotal
    private BigDecimal total;

    // total cart weight
    private BigDecimal totalWeightKg;

    // calculated shipping
    private BigDecimal shippingCost;

    // subtotal + shipping
    private BigDecimal grandTotal;
}