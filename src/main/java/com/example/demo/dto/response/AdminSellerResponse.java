package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminSellerResponse {
    private Integer id;
    private Integer userId;
    private String userEmail;
    private String userName;
    private String shopName;
    private String shopDescription;
    private String shopLogo;
    private String businessName;
    private String businessRegistrationNumber;
    private String taxId;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String status;
    private BigDecimal commissionRate;
    private String rejectionReason;
    private String suspensionReason;
    private LocalDateTime approvedAt;
    private Integer totalProducts;
    private Integer totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal rating;
    private LocalDateTime createdAt;
}
