package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productId;

    private Integer quantityChange; // + or - stock

    private String reason; // PURCHASE, RETURN, ADJUSTMENT

    private Integer orderId;

    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}