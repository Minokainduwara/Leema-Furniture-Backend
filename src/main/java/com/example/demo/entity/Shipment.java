package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "shipping_method_id")
    private Integer shippingMethodId;

    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;

    @Column(length = 100)
    private String carrier;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status = ShipmentStatus.pending;

    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ShipmentStatus {
        pending, processing, shipped, in_transit, delivered, failed, returned
    }
}
