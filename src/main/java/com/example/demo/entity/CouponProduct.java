package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "coupon_products", uniqueConstraints = {
        @UniqueConstraint(name = "unique_coupon_product", columnNames = {"coupon_id", "product_id"})
})
public class CouponProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_coupon_products_coupon_id"))
    private Coupon coupon;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    public CouponProduct() {
        // Required by JPA
    }

    public CouponProduct(Coupon coupon, Integer productId) {
        this.coupon = coupon;
        this.productId = productId;
    }
}