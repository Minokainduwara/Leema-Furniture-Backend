package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminRevenueAnalyticsResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
    private BigDecimal sellerPayouts;
    private List<RevenueBreakdownPoint> breakdown;
}
