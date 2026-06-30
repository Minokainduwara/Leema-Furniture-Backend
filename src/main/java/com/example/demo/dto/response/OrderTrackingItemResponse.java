package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderTrackingItemResponse {

    private Integer productId;

    private String productName;

    private String productImage;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal total;
}