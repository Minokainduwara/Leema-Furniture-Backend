package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class AdminSalesAnalyticsResponse {
    private LocalDate date;
    private Integer totalOrders;
    private BigDecimal totalSales;
    private BigDecimal totalRevenue;
    private Integer totalCustomers;
    private BigDecimal avgOrderValue;
    private Integer totalItemsSold;
}
