package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "coupon_categories", uniqueConstraints = {
        @UniqueConstraint(name = "unique_coupon_category", columnNames = {"coupon_id", "category_id"})
})
public class CouponCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false,
            foreignKey = @ForeignKey(name = "FK_coupon_categories_coupon_id"))
    private Coupon coupon;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    public CouponCategory() {
        // Required by JPA
    }

    public CouponCategory(Coupon coupon, Integer categoryId) {
        this.coupon = coupon;
        this.categoryId = categoryId;
    }
}