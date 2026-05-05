package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminDashboardResponse {
    private Long totalUsers;
    private Long totalSellers;
    private Long activeSellers;
    private Long pendingSellers;
    private Long totalProducts;
    private Long totalOrders;
    private Long pendingOrders;
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal monthRevenue;
    private Long totalPayments;
    private Long pendingRefunds;
    private Long lowStockProducts;
    private List<SalesDataPoint> recentSales;
}
