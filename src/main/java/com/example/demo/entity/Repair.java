package com.example.demo.entity;

import com.example.demo.enums.RepairStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repairs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Customer who requested repair
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Seller/technician handling repair
    @ManyToOne
    @JoinColumn(name = "handled_by")
    private User handledBy;

    // Repair related product (optional but useful)
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "issue_description")
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    @Column(name = "estimated_cost")
    private Double estimatedCost;

    @Column(name = "actual_cost")
    private Double actualCost;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = RepairStatus.REQUESTED;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}