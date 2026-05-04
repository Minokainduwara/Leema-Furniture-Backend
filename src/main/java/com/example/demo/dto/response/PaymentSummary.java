package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummary {
    private Integer id;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String gateway;
    private String gatewayTransactionId;
    private LocalDateTime createdAt;
}
