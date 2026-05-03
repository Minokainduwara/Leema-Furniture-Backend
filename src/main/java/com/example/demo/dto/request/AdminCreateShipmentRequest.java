package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminCreateShipmentRequest {
    @NotNull
    private Integer orderId;
    private Integer shippingMethodId;
    private String trackingNumber;
    private String carrier;
    private BigDecimal shippingCost;
    private String estimatedDeliveryDate;
}