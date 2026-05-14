package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponse {

    private Integer paymentId;

    private Integer orderId;

    private String status;

    private BigDecimal amount;

    private String gateway;

    private String transactionId;
}