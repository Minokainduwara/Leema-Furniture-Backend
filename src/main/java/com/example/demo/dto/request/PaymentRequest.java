package com.example.demo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    private Integer orderId;

    private Integer paymentMethodId;

    private BigDecimal amount;

    private String gateway;
}