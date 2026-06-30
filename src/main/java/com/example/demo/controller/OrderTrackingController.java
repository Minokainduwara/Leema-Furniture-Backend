package com.example.demo.controller;

import com.example.demo.dto.response.OrderTrackingItemResponse;
import com.example.demo.dto.response.OrderTrackingResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lives outside the protected OrderController so we don't have to modify it.
 * Provides GET /api/orders/{id}/tracking used by OrderTrackingPage.tsx.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderTrackingController {

    private final OrderRepository orderRepository;

    @GetMapping("/{id}/tracking")
    public ResponseEntity<OrderTrackingResponse> tracking(@PathVariable Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        List<OrderTrackingItemResponse> items = order.getOrderItems() == null
                ? List.of()
                : order.getOrderItems().stream().map(this::toItem).toList();

        String shipping = null;
        if (order.getShippingAddress() != null) {
            shipping = String.join(", ",
                    order.getShippingAddress().getStreetAddress(),
                    order.getShippingAddress().getCity(),
                    order.getShippingAddress().getCountry());
        }

        OrderTrackingResponse response = OrderTrackingResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getStatus() == null ? null : order.getStatus().name())
                .paymentStatus(order.getPaymentStatus() == null ? null : order.getPaymentStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .customerName(order.getUser() == null ? null : order.getUser().getName())
                .phoneNumber(order.getUser() == null ? null : order.getUser().getPhoneNumber())
                .shippingAddress(shipping)
                .items(items)
                .build();

        return ResponseEntity.ok(response);
    }

    private OrderTrackingItemResponse toItem(OrderItem oi) {
        BigDecimal total = oi.getTotal();
        if (total == null) {
            BigDecimal unit = oi.getUnitPrice() == null ? BigDecimal.ZERO : oi.getUnitPrice();
            total = unit.multiply(BigDecimal.valueOf(oi.getQuantity()));
        }
        return OrderTrackingItemResponse.builder()
                .productId(oi.getProduct() == null ? null : oi.getProduct().getId())
                .productName(oi.getProduct() == null ? null : oi.getProduct().getName())
                .productImage(oi.getProduct() == null ? null : oi.getProduct().getImage())
                .quantity(oi.getQuantity())
                .unitPrice(oi.getUnitPrice())
                .total(total)
                .build();
    }
}
