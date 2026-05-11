package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SellerDashboardResponse {
    private BigDecimal totalSales;
    private long totalProducts;
    private long totalOrders;
    private long totalCategories;
}