package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderTrackingResponse {

    private Integer orderId;

    private String orderNumber;

    private String orderStatus;

    private String paymentStatus;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private String customerName;

    private String phoneNumber;

    private String shippingAddress;

    private List<OrderTrackingItemResponse> items;
}