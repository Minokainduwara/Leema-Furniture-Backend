package com.example.demo.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    private Integer id;
    private String orderNumber;

    // User
    private Integer userId;
    private String userEmail;
    private String userName;

    // Financials
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    // Status
    private String status;
    private String paymentStatus;

    // Notes
    private String customerNotes;
    private String adminNotes;         // admin only — excluded for user responses

    // Related objects (populated on detail views)
    private AddressResponse shippingAddress;
    private AddressResponse billingAddress;
    private ShippingMethodSummary shippingMethod;
    private CouponSummary coupon;
    private List<OrderItemResponse> items;
    private List<OrderHistoryResponse> history;
    private ShipmentSummary shipment;
    private PaymentSummary payment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
