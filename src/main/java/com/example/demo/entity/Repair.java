package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repairs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User who requested repair
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Related product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // Related order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // Staff handling repair
    @ManyToOne
    @JoinColumn(name = "handled_by")
    private User handledBy;

    @Column(name = "issue_description")
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    private RepairStatus status = RepairStatus.REQUESTED;

    @Column(name = "actual_cost")
    private Double actualCost;

    @Column(name = "estimated_cost")
    private Double estimatedCost;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private ServiceType type;

    public enum ServiceType {
        REPAIR,
        REFUND,
        REINSTALLMENT
    }
    public enum RepairStatus {
        REQUESTED,
        IN_PROGRESS,
        COMPLETED,
        REJECTED
    }
}