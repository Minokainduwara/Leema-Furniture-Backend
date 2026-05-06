package com.example.demo.repository;
import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // ── Admin: multi-field filter across all products ─────────────────────────
    @Query("""
            SELECT p FROM Product p
            WHERE (:status     IS NULL OR p.status      = :status)
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND p.deletedAt IS NULL
            """)
    Page<Product> findWithAdminFilters(
            @Param("status") Product.ProductStatus status,
            @Param("categoryId") Integer categoryId,
            Pageable pageable);


    // ── Analytics: top-selling products ──────────────────────────────────────
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL ORDER BY p.totalSales DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    // ── Browsing: active products by category ─────────────────────────────────
    Page<Product> findByCategoryIdAndStatus(
            Integer categoryId,
            Product.ProductStatus status,
            Pageable pageable);

    // ── SKU uniqueness check ──────────────────────────────────────────────────
    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Integer id);
}