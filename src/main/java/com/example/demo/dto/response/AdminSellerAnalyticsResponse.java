package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminSellerAnalyticsResponse {
    private Integer sellerId;
    private String shopName;
    private Integer totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal commissionEarned;
    private Integer totalProducts;
    private BigDecimal rating;
}
