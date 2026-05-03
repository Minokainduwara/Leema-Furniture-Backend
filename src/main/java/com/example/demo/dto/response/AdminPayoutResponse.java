package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminPayoutResponse {
    private Integer id;
    private Integer sellerId;
    private String shopName;
    private BigDecimal amount;
    private BigDecimal commissionDeducted;
    private BigDecimal netAmount;
    private String status;
    private String payoutMethod;
    private String referenceNumber;
    private String notes;
    private LocalDateTime periodFrom;
    private LocalDateTime periodTo;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}
