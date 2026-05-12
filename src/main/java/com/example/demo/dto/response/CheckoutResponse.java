package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CheckoutResponse {

    private Integer orderId;

    private String orderNumber;

    private String paymentMethod;

    private String paymentStatus;

    private String orderStatus;

    private String message;
}