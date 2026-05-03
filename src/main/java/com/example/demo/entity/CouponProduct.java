package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coupon_products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_coupon_product",
                        columnNames = {"coupon_id", "product_id"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Many mappings belong to one coupon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    // Many mappings can reference one product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
