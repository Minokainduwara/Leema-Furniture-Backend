package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminSellerDetailResponse {
    private AdminSellerResponse seller;
    private Long totalProducts;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal totalPayouts;
    private BigDecimal pendingPayouts;
}
