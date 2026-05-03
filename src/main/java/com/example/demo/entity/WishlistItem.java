package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "wishlist_items", uniqueConstraints = {
        @UniqueConstraint(name = "unique_wishlist_product", columnNames = {"wishlist_id", "product_id"})
})
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    public WishlistItem() {
        // Required by JPA.
    }
}