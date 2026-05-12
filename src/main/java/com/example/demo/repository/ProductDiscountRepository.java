package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductDiscount;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Integer> {

    @Query("""
        SELECT pd FROM ProductDiscount pd
        WHERE pd.product.id = :productId
        AND pd.active = true
        AND :now BETWEEN pd.startDate AND pd.endDate
    """)
    ProductDiscount findActiveDiscount(
            @Param("productId") Integer productId,
            @Param("now") LocalDateTime now
    );
    ProductDiscount findByProduct(Product product);
    void deleteByProductId(Integer productId);
    Optional<ProductDiscount> findByProductId(Integer productId);

}