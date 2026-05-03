package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "returns",
        indexes = {
                @Index(name = "idx_order_id", columnList = "order_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Return belongs to one order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Return requested by user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status = ReturnStatus.requested;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    // One return has many return items
    @OneToMany(mappedBy = "returnRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ReturnItem> returnItems;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ReturnStatus {
        requested,
        approved,
        rejected,
        in_transit,
        received,
        refunded,
        cancelled
    }
}
