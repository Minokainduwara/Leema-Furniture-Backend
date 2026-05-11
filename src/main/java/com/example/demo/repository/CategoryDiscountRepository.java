package com.example.demo.repository;

import com.example.demo.entity.CategoryDiscount;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CategoryDiscountRepository extends JpaRepository<CategoryDiscount, Integer> {

    @Query("""
        SELECT cd FROM CategoryDiscount cd
        WHERE cd.category.id = :categoryId
        AND cd.active = true
        AND :now BETWEEN cd.startDate AND cd.endDate
    """)
    CategoryDiscount findActiveDiscount(
            @Param("categoryId") Integer categoryId,
            @Param("now") LocalDateTime now
    );
    Optional<CategoryDiscount> findByCategoryId(Integer categoryId);
}