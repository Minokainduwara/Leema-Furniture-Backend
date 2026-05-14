package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminProductAnalyticsResponse {
    private Integer productId;
    private String productName;
    private String category;
    private Integer totalSales;
    private BigDecimal totalRevenue;
    private Integer stock;
    private BigDecimal rating;
}
